package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime end);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.start_time <= now() and i.id = ? and b.status = 'APPROVED' " +
            "order by b.end_time desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(Long itemId);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.start_time > now() and i.id = ? and b.status = 'APPROVED' " +
            "order by b.start_time " +
            "limit 1", nativeQuery = true)
    Booking findNextBooking(Long itemId);

    List<Booking> findFirstByStartLessThanEqualAndStatusEqualsAndItemInOrderByEndDesc(LocalDateTime time, BookingStatus status, List<Item> items);

    List<Booking> findFirstByStartAfterAndStatusEqualsAndItemInOrderByStart(LocalDateTime time, BookingStatus status, List<Item> items);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.end_time <= now() and i.id = ? and b.booker_id = ? and b.status = 'APPROVED'" +
            "limit 1", nativeQuery = true)
    Booking findCompletedBooking(Long itemId, Long bookerId);
}
