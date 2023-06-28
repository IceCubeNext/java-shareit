package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item {}, userId={}", id, userId);
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(value = "text") String text,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Search items with text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Validated(Marker.OnCreate.class) ItemRequestDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Validated(Marker.OnCreate.class) CommentRequestDto commentDto,
                                             @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating comment {} for item={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody @Validated(Marker.OnUpdate.class) ItemRequestDto itemDto,
                                             @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        return itemClient.deleteItem(id);
    }
}
