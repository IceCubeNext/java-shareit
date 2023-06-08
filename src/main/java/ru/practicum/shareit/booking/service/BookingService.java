package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto getBookingById(Long id, Long userId);

    List<BookingDto> getUserBookings(Long userId, BookingState state);

    List<BookingDto> getBookingsByOwner(Long userId, BookingState state);

    BookingDto addBooking(BookingCreateDto bookingDto, Long userId);

    BookingDto updateBooking(Long id, Long userId, Boolean isApproved);
}
