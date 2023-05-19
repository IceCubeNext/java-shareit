package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item getItemById(Long id) {
        if (itemRepository.containsItem(id)) {
            return itemRepository.getItemById(id);
        } else {
            throw new NotFoundException(String.format("Item with id=%d not found", id));
        }
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        if (userRepository.containsUser(userId)) {
            return itemRepository.getItemsByUserId(userId);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text);
    }

    @Override
    public Item addItem(Item item) {
        if (userRepository.containsUser(item.getOwner())) {
            return itemRepository.addItem(item);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", item.getOwner()));
        }
    }

    @Override
    public Item updateItem(Long userId, Long id, Item item) {
        if (userRepository.containsUser(userId)) {
            return itemRepository.updateItem(userId, id, item);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }

    }

    @Override
    public Item deleteItem(Long id) {
        if (itemRepository.containsItem(id)) {
            return itemRepository.deleteItem(id);
        } else {
            throw new NotFoundException(String.format("Item with id=%d not found", id));
        }
    }
}
