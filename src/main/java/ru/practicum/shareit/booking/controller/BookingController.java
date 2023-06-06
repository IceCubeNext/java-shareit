package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.Marker;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@PathVariable(value = "bookingId") Long id,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    List<BookingDto> getUserBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwner(userId, state);
    }

    @PostMapping
    BookingDto addBooking(@Validated(Marker.OnCreate.class) @RequestBody BookingCreateDto bookingDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDto approveBooking(@PathVariable(value = "bookingId") Long id,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam(value = "approved") Boolean isApproved) {
        return bookingService.updateBooking(id, userId, isApproved);
    }
}
