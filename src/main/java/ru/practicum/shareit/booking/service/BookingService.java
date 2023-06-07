package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.List;

public interface BookingService {
    BookingDto getBookingById(Long id, Long userId);

    List<BookingDto> getUserBookings(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long userId, String state);

    BookingDto addBooking(BookingCreateDto bookingDto, Long userId);

    BookingDto updateBooking(Long id, Long userId, Boolean isApproved);
}
