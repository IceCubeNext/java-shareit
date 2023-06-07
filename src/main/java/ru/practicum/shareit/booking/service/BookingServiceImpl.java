package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
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
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingState state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("User with id=%d not found", ownerId));
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto addBooking(BookingCreateDto bookingDto, Long userId) {
        User user = getUser(userId);
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
        User user = getUser(userId);
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

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", id)));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", id)));
    }
}
