package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(Long id);

    List<Item> getItemsByUserId(Long userId);

    List<Item> searchItems(String text);

    Item addItem(Item item);

    Item updateItem(Long userId, Long id, Item item);

    Item deleteItem(Long id);
}
