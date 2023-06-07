package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime end);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.start_time < now() and i.id = ? and b.status = 'APPROVED' " +
            "order by b.end_time desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(Long itemId);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.start_time > now() and i.id = ? and b.status = 'APPROVED' " +
            "order by b.start_time " +
            "limit 1", nativeQuery = true)
    Booking findNextBooking(Long itemId);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.end_time <= now() and i.id = ? and b.booker_id = ? and b.status = 'APPROVED'" +
            "limit 1", nativeQuery = true)
    Booking findCompletedBooking(Long itemId, Long bookerId);
}
