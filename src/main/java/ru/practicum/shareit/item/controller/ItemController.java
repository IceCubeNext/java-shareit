package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Marker;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @Validated(Marker.OnUpdate.class) @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
