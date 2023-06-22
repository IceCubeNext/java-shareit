package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
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
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id, Long userId) {
        Booking booking = getBooking(id);
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return BookingMapper.mapToBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("Booking associated with userId=%d not found", userId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, BookingState state, Integer from, Integer size) {
        userService.getUser(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        Page<Booking> bookings;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, currentTime, currentTime, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentTime, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentTime, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookings.map(BookingMapper::mapToBookingDto).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingState state, Integer from, Integer size) {
        userService.getUser(ownerId);
        LocalDateTime currentTime = LocalDateTime.now();
        Page<Booking> bookings;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, currentTime, currentTime, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, currentTime, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, currentTime, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, page);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookings.map(BookingMapper::mapToBookingDto).getContent();
    }

    @Override
    @Transactional
    public BookingDto addBooking(BookingCreateDto bookingDto, Long userId) {
        User user = userService.getUser(userId);
        Item item = getItem(bookingDto.getItemId());
        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new NotFoundException(String.format("Item with id=%d is not available", item.getId()));
        }
        if (!item.getAvailable()) {
            throw new IllegalArgumentException(String.format("Item with id=%d is not available", item.getId()));
        }
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, user);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long id, Long userId, Boolean isApproved) {
        Booking booking = getBooking(id);
        User user = userService.getUser(userId);
        if (booking.getItem().getOwner().equals(user)) {
            if (isApproved) {
                if (booking.getStatus().equals(BookingStatus.WAITING)) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    throw new IllegalArgumentException("Booking not waiting approve");
                }
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new NotFoundException(String.format("Booking associated with userId=%d not found", userId));
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id=%d not found", id)));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", id)));
    }
}
