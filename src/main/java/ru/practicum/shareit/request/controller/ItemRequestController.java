package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.Marker;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Long requestId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequests(@RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from < 0 || size < 0) {
            throw new IllegalArgumentException("Page parameters incorrect");
        }
        return itemRequestService.getRequests(userId, from, size);
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from < 0 || size < 0) {
            throw new IllegalArgumentException("Page parameters incorrect");
        }
        return itemRequestService.getUserRequests(userId, from, size);
    }

    @PostMapping
    public RequestDto addRequest(@Validated(Marker.OnCreate.class) @RequestBody RequestDto requestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.addRequest(requestDto, userId);
    }

}
