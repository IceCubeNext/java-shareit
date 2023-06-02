package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    void deleteItem(Long id);
}
