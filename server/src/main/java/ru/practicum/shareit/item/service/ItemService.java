package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemInfoDto getItemById(Long id, Long userId);

    List<ItemInfoDto> getItemsByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    ItemDto addItem(Long userId, ItemDto itemDto);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    void deleteItem(Long id);
}
