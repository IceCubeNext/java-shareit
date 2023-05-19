package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utility.ItemMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper = new ItemMapper();

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return itemMapper.mapToItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId).stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text", defaultValue = "") String text) {
        List<Item> items = itemService.searchItems(text);
        return items.stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.mapToItem(itemDto);
        item.setOwner(userId);
        return itemMapper.mapToItemDto(itemService.addItem(item));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        if (userId == 0) throw new IllegalArgumentException("User id not Found");
        Item item = itemMapper.mapToItem(itemDto);
        return itemMapper.mapToItemDto(itemService.updateItem(userId, id, item));
    }

    @DeleteMapping("{id}")
    public ItemDto deleteItem(@PathVariable Long id) {
        Item item = itemService.deleteItem(id);
        return itemMapper.mapToItemDto(item);
    }
}
