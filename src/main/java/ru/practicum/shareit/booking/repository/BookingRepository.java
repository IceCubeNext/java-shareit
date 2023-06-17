package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable page);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable page);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime end, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable page);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable page);

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
