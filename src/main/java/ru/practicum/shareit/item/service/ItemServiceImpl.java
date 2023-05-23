package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item getItemById(Long id) {
        checkItem(id);
        return itemRepository.getItemById(id);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        checkUser(userId);
        return itemRepository.getItemsByUserId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text);
    }

    @Override
    public Item addItem(Item item) {
        checkUser(item.getOwner());
        return itemRepository.addItem(item);
    }

    @Override
    public Item updateItem(Long userId, Long id, Item item) {
        checkItem(id);
        checkUser(userId);
        return itemRepository.updateItem(userId, id, item);
    }

    @Override
    public Item deleteItem(Long id) {
        checkItem(id);
        return itemRepository.deleteItem(id);
    }

    private void checkUser(Long id) {
        if (!userRepository.containsUser(id)) {
            throw new NotFoundException(String.format("User with id=%d not found", id));
        }
    }

    private void checkItem(Long id) {
        if (!itemRepository.containsItem(id)) {
            throw new NotFoundException(String.format("Item with id=%d not found", id));
        }
    }
}
