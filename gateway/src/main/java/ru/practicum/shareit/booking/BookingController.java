package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable(value = "bookingId") Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking {}, userId={}", id, userId);
        return bookingClient.getBooking(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state={}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings with state={}, ownerId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestBody @Validated(Marker.OnCreate.class) BookingRequestDto requestDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable(value = "bookingId") Long id,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "approved") Boolean isApproved) {
        log.info("Approved booking {}, userId={}, isApprove={}", id, userId, isApproved);
        return bookingClient.approveBooking(userId, id, isApproved);
    }
}
