package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status);
    List<Booking> findAllByBookerId(Long userId);
    List<Booking> findAllByBookerIdAndStartAfterAndEndBefore(Long userId, Timestamp start, Timestamp end);
    List<Booking> findAllByBookerIdAndEndBefore(Long userId, Timestamp end);
    List<Booking> findAllByBookerIdAndStartAfter(Long userId, Timestamp end);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);
    List<Booking> findAllByItemOwnerId(Long ownerId);
    List<Booking> findAllByItemOwnerIdAndStartAfterAndEndBefore(Long ownerId, Timestamp start, Timestamp end);
    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, Timestamp end);
    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, Timestamp end);
}
