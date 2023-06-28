package ru.practicum.shareit.booking.utility;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    @Test
    public void mapToBookingDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        LocalDateTime time = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(time);
        booking.setEnd(time);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
    }

    @Test
    public void mapToBookingShortDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        LocalDateTime time = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(time);
        booking.setEnd(time);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        BookingShortDto bookingShortDto = BookingMapper.mapToBookingShortDto(booking);
        assertEquals(booking.getId(), bookingShortDto.getId());
        assertEquals(booking.getItem().getId(), bookingShortDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingShortDto.getBookerId());
    }

    @Test
    public void mapToBookingTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        LocalDateTime time = LocalDateTime.now();
        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setStart(time);
        bookingDto.setEnd(time);
        bookingDto.setItemId(1L);

        Booking booking = BookingMapper.mapToBooking(bookingDto, item, user);
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(item.getId(), booking.getItem().getId());
        assertEquals(user.getId(), booking.getBooker().getId());
    }
}