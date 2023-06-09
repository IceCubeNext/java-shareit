package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.utility.Constants.USER_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Long requestId,
                                     @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequests(@RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getRequests(userId, from, size);
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @PostMapping
    public RequestDto addRequest(@RequestBody RequestDto requestDto,
                                 @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.addRequest(requestDto, userId);
    }
}