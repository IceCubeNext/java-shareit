package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
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
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return new ArrayList<>(userItems.getOrDefault(userId, Map.of()).values());
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
        Map<Long, Item> usrItems = userItems.computeIfAbsent(item.getOwner(), k -> new HashMap<>());
        usrItems.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long id, Item item) {
        Item itm = items.get(id);
        if (!Objects.equals(userId, itm.getOwner())) {
            throw new NotFoundException("Attempt to change owner of Item");
        }
        if (StringUtils.hasText(item.getName()) && !item.getName().equals(itm.getName())) {
            itm.setName(item.getName());
        }
        if (StringUtils.hasText(item.getDescription()) && !item.getDescription().equals(itm.getDescription())) {
            itm.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && item.getAvailable() != itm.getAvailable()) {
            itm.setAvailable(item.getAvailable());
        }
        return itm;
    }

    @Override
    public Item deleteItem(Long id) {
        Item item = items.remove(id);
        userItems.get(item.getOwner()).remove(id);
        return item;
    }

    private Long getNewId() {
        return ++this.id;
    }
}
