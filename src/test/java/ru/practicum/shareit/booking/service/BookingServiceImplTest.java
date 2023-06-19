package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utility.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Booking booking;
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("User");
        owner.setEmail("mail@mail.ru");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.ru");

        item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    public void getBookingByIdOwner() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto actual = bookingService.getBookingById(booking.getId(), owner.getId());
        BookingDto expected = BookingMapper.mapToBookingDto(booking);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getBookingByIdBooker() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto actual = bookingService.getBookingById(booking.getId(), booker.getId());
        BookingDto expected = BookingMapper.mapToBookingDto(booking);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getBookingByIdUnknownUserId() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), 99L));
    }

    @Test
    public void getBookingByIdUnknownId () {
        when(bookingRepository.findById(99L))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(99L, booker.getId()));
    }

    @Test
    public void getUserBookingsUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(99L, BookingState.ALL, 0, 10));
    }

    @Test
    public void getUserBookingsUnknownStatus() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.getUserBookings(1L, BookingState.valueOf("Unknown"), 0, 10));
    }

    @Test
    public void getUserBookingsAllStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(eq(booking.getId()), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getUserBookings(owner.getId(), BookingState.ALL, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsCurrentStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booking.getId()), any(), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getUserBookings(owner.getId(), BookingState.CURRENT, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsPastStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getUserBookings(owner.getId(), BookingState.PAST, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsFutureStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getUserBookings(owner.getId(), BookingState.FUTURE, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsWaitingStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getUserBookings(owner.getId(), BookingState.WAITING, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsRejectedStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getUserBookings(owner.getId(), BookingState.REJECTED, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsByOwnerUnknownStatus() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingsByOwner(1L, BookingState.valueOf("Unknown"), 0, 10));
    }

    @Test
    public void getUserBookingsByOwnerAllStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(booking.getId()), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getBookingsByOwner(owner.getId(), BookingState.ALL, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsByOwnerCurrentStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booking.getId()), any(), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getBookingsByOwner(owner.getId(), BookingState.CURRENT, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsByOwnerPastStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getBookingsByOwner(owner.getId(), BookingState.PAST, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsByOwnerFutureStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getBookingsByOwner(owner.getId(), BookingState.FUTURE, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsByOwnerWaitingStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getBookingsByOwner(owner.getId(), BookingState.WAITING, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void getUserBookingsByOwnerRejectedStatus() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(booking.getId()), any(), any()))
                .thenReturn(page);

        BookingDto actual = bookingService.getBookingsByOwner(owner.getId(), BookingState.REJECTED, 0, 10).get(0);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void addBookings() {
        when(userService.getUser(booker.getId()))
                .thenReturn(booker);

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        BookingDto actual = bookingService.addBooking(bookingCreateDto, booker.getId());
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
    }

    @Test
    public void addBookingsOwnerBookOwnItem() {
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, owner.getId()));
    }

    @Test
    public void addBookingsItemUnavailable() {
        when(userService.getUser(booker.getId()))
                .thenReturn(booker);

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        item.setAvailable(false);

        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(bookingCreateDto, booker.getId()));
    }

    @Test
    public void addBookingsUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, 99L));
    }

    @Test
    public void addBookingsItemNotFound() {
        when(userService.getUser(booker.getId()))
                .thenReturn(booker);

        when(itemRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(99L);
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, booker.getId()));
    }

    @Test
    public void updateBookingApprove() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        BookingDto actual = bookingService.updateBooking(booking.getId(), owner.getId(), true);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void updateBookingRejected() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        BookingDto actual = bookingService.updateBooking(booking.getId(), owner.getId(), false);
        BookingDto expected = BookingMapper.mapToBookingDto(booking);
        expected.setStatus(BookingStatus.REJECTED);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem().getId(), actual.getItem().getId());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void updateBookingBookingNotFound() {
        when(bookingRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(99L, owner.getId(), true));
    }

    @Test
    public void updateBookingBookingUserNotFound() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(booking.getId(), 99L, true));
    }

    @Test
    public void updateBookingNotOwner() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(userService.getUser(booker.getId()))
                .thenReturn(booker);

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(booking.getId(), booker.getId(), true));
    }

    @Test
    public void updateBookingNotWaitingApprove() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        booking.setStatus(BookingStatus.CANCELED);

        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBooking(booking.getId(), owner.getId(), true));
    }
}