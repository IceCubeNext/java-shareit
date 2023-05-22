package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return ItemMapper.mapToItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.mapToItemsDto(itemService.getItemsByUserId(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        return ItemMapper.mapToItemsDto(itemService.searchItems(text));
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.mapToItemDto(itemService.addItem(item));
    }

    @PatchMapping("/{id}")
    @Validated(Marker.OnUpdate.class)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        return ItemMapper.mapToItemDto(itemService.updateItem(userId, id, item));
    }

    @DeleteMapping("{id}")
    public ItemDto deleteItem(@PathVariable Long id) {
        Item item = itemService.deleteItem(id);
        return ItemMapper.mapToItemDto(item);
    }
}
