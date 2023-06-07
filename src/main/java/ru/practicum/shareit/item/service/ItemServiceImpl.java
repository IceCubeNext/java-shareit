package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utility.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemInfoDto getItemById(Long id, Long userId) {
        Item item = getItem(id);
        ItemInfoDto itemInfoDto = ItemMapper.mapToItemInfoDto(item);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            Booking last = bookingRepository.findLastBooking(item.getId());
            Booking next = bookingRepository.findNextBooking(item.getId());
            if (last != null) {
                itemInfoDto.setLastBooking(BookingMapper.mapToBookingShortDto(last));
            }
            if (next != null) {
                itemInfoDto.setNextBooking(BookingMapper.mapToBookingShortDto(next));
            }
        }
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(ItemMapper::mapToCommentDto)
                .collect(toList());
        itemInfoDto.setComments(comments);
        return itemInfoDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemInfoDto> getItemsByUserId(Long userId) {
        User user = getUser(userId);
        List<Item> items = itemRepository.findAllByOwnerOrderById(user);

        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        Map<Item, List<Booking>> lasts = bookingRepository.findFirstByStartBeforeAndStatusEqualsAndItemInOrderByEndDesc(
                        LocalDateTime.now(), BookingStatus.APPROVED, items)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        Map<Item, List<Booking>> following = bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemInOrderByStart(
                        LocalDateTime.now(), BookingStatus.APPROVED, items)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<ItemInfoDto> itemsInfoDto = new ArrayList<>();
        for (Item item : items) {
            ItemInfoDto itemDto = ItemMapper.mapToItemInfoDto(item);
            if (comments.containsKey(item)) {
                itemDto.setComments(comments.get(item).stream().map(ItemMapper::mapToCommentDto).collect(toList()));
            } else {
                itemDto.setComments(Collections.emptyList());
            }

            if (lasts.containsKey(item)) {
                if (lasts.get(item).size() > 0) {
                    Booking last = lasts.get(item).get(0);
                    BookingShortDto bookingShortDtoLast = BookingMapper.mapToBookingShortDto(last);
                    itemDto.setLastBooking(bookingShortDtoLast);
                }
            }

            if (following.containsKey(item)) {
                if (following.get(item).size() > 0) {
                    Booking next = following.get(item).get(0);
                    BookingShortDto bookingShortDtoNext = BookingMapper.mapToBookingShortDto(next);
                    itemDto.setNextBooking(bookingShortDtoNext);
                }
            }
            itemsInfoDto.add(itemDto);
        }
        return itemsInfoDto;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.mapToItemDto(itemRepository.findText(text));
    }

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User user = getUser(userId);
        Item item = ItemMapper.mapToItem(itemDto, user);
        item.setOwner(user);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        User author = getUser(userId);
        Item item = getItem(itemId);
        if (bookingRepository.findCompletedBooking(item.getId(), author.getId()) != null) {
            Comment comment = ItemMapper.mapToComment(commentDto, author, item);
            return ItemMapper.mapToCommentDto(commentRepository.save(comment));
        }
        throw new IllegalArgumentException(
                String.format("Booking with userId=%d and ItemId=%d not found", userId, itemId));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        Item item = getItem(id);
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new NotFoundException(String.format("Item with userId=%d not found", userId));
        }
        if (StringUtils.hasText(itemDto.getName()) && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (StringUtils.hasText(itemDto.getDescription()) && !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && itemDto.getAvailable() != item.getAvailable()) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(Long id) {
        Item item = getItem(id);
        itemRepository.delete(item);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", id)));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", id)));
    }
}
