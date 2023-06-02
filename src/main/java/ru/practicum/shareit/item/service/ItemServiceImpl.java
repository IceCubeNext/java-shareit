package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(Long id) {
        Item item = getItem(id);
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        User user = getUser(userId);
        return ItemMapper.mapToItemDto(itemRepository.findAllByOwner(user));
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

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", id)));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", id)));
    }
}
