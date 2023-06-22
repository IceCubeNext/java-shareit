package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Marker;
import ru.practicum.shareit.validation.PageValidation;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemInfoDto getItemById(@PathVariable Long id,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getItems(@RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        PageValidation.validatePageParameters(from, size);
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(value = "text") String text) {
        PageValidation.validatePageParameters(from, size);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping
    public ItemDto addItem(@Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Validated(Marker.OnCreate.class) @RequestBody CommentCreateDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@Validated(Marker.OnUpdate.class) @RequestBody ItemDto itemDto,
                              @PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
