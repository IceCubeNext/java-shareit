package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

import java.util.*;
import java.util.stream.Collectors;

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
        setItemInfo(itemInfoDto, userId);
        return itemInfoDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemInfoDto> getItemsByUserId(Long userId) {
        User user = getUser(userId);
        List<ItemInfoDto> items = itemRepository.findAllByOwner(user).stream()
                .map(ItemMapper::mapToItemInfoDto)
                .sorted(Comparator.comparing(ItemInfoDto::getId))
                .collect(Collectors.toList());
        for (ItemInfoDto item : items) {
            setItemInfo(item, userId);
        }
        return items;
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
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long id) {
        Item item = getItem(id);
        itemRepository.delete(item);
    }

    private void setItemInfo(ItemInfoDto item, Long userId) {
        if (Objects.equals(item.getOwner(), userId)) {
            Booking last = bookingRepository.findLastBooking(item.getId());
            Booking next = bookingRepository.findNextBooking(item.getId());
            if (last != null) {
                item.setLastBooking(new BookingShortDto(last.getId(), last.getItem().getId(), last.getBooker().getId()));
            }
            if (next != null) {
                item.setNextBooking(new BookingShortDto(next.getId(), next.getItem().getId(), next.getBooker().getId()));
            }
        }
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(ItemMapper::mapToCommentDto)
                .collect(Collectors.toList());
        item.setComments(comments);
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
