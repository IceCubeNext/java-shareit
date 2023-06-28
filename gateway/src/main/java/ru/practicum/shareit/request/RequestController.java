package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests with userId={}, from={}, size={}", userId, from, size);
        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get user requests with userId={}", userId);
        return requestClient.getUserRequests(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Validated(Marker.OnCreate.class) RequestDto requestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Add request {} with userId={}", requestDto, userId);
        return requestClient.addRequest(userId, requestDto);
    }

}
