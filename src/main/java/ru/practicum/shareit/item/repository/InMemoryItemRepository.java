package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Map<Long, Item>> userItems = new HashMap<>();
    private Long id = 0L;

    @Override
    public Boolean containsItem(Long id) {
        return items.containsKey(id);
    }

    @Override
    public Item getItemById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException(String.format("Item with id=%d not found", id));
        }
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        if (userItems.containsKey(userId)) {
            return new ArrayList<>(userItems.get(userId).values());
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(x -> x.getName() != null && x.getName().toLowerCase().contains(text.toLowerCase())
                        || x.getDescription() != null && x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item addItem(Item item) {
        Long id = getNewId();
        item.setId(id);
        items.put(id, item);
        if (userItems.containsKey(item.getOwner())) {
            userItems.get(item.getOwner()).put(id, item);
        } else {
            userItems.put(item.getOwner(), new HashMap<>());
            userItems.get(item.getOwner()).put(id, item);
        }
        return items.get(id);
    }

    @Override
    public Item updateItem(Long userId, Long id, Item item) {
        if (items.containsKey(id)) {
            Item itm = items.get(id);
            if (!Objects.equals(userId, itm.getOwner())) {
                throw new NotFoundException("Attempt to change owner of Item");
            }
            if (item.getName() != null && !item.getName().equals(itm.getName())) {
                itm.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().equals(itm.getDescription())) {
                itm.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null && item.getAvailable() != itm.getAvailable()) {
                itm.setAvailable(item.getAvailable());
            }
        } else {
            throw new NotFoundException(String.format("Item with id=%d not found", id));
        }
        return items.get(id);
    }

    @Override
    public Item deleteItem(Long id) {
        if (items.containsKey(id)) {
            Item item = items.remove(id);
            userItems.get(item.getOwner()).remove(id);
            return item;
        } else {
            throw new NotFoundException(String.format("Item with id=%d not found", id));
        }
    }

    private Long getNewId() {
        return ++this.id;
    }
}
