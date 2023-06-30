package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.utility.Constants.USER_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@PathVariable(value = "bookingId") Long id,
                              @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    List<BookingDto> getUserBookings(@RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                     @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getBookingsByOwner(@RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                        @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping
    BookingDto addBooking(@RequestBody BookingCreateDto bookingDto,
                          @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDto approveBooking(@PathVariable(value = "bookingId") Long id,
                              @RequestHeader(USER_HEADER) Long userId,
                              @RequestParam(value = "approved") Boolean isApproved) {
        return bookingService.updateBooking(id, userId, isApproved);
    }
}
