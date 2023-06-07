package ru.practicum.shareit.booking.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking mapToBooking(BookingCreateDto bookingDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserShortDto(booking.getBooker().getId(), booking.getBooker().getName()),
                new ItemShortDto(booking.getItem().getId(), booking.getItem().getName())
        );
    }

    public BookingShortDto mapToBookingShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getBooker().getId()
        );
    }
}
