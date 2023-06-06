package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utility.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
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
    public List<BookingDto> getUserBookings(Long userId, String state) {
        List<Booking> bookings;
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId);
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now());
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now());
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner (Long ownerId, String state) {
        List<Booking> bookings;
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("User with id=%d not found", ownerId));
        }
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(ownerId);
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(), LocalDateTime.now());
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now());
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now());
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                return bookings.stream()
                        .map(BookingMapper::mapToBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public BookingDto addBooking(BookingCreateDto bookingDto, Long userId) {
        User user = getUser(userId);
        Item item = getItem(bookingDto.getItemId());
        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new NotFoundException(String.format("Item with id=%d is not available", item.getId()));
        }
        if (!item.getAvailable()) {
            throw new IllegalArgumentException(String.format("Item with id=%d is not available", item.getId()));
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart()) || bookingDto.getStart().isAfter((bookingDto.getEnd()))) {
            throw new IllegalArgumentException("Start of booking should be before than end");
        }
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, user);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long id, Long userId, Boolean isApproved) {
        Booking booking = getBooking(id);
        User user = getUser(userId);
        if (booking.getItem().getOwner().equals(user)) {
            if (isApproved) {
                if (booking.getStatus().equals(BookingStatus.WAITING)) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    throw new IllegalArgumentException("Booking not wait approve");
                }
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new NotFoundException(String.format("Booking associated with userId=%d not found", userId));
        }
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
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
