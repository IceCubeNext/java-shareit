package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_time")
    private Timestamp start;
    @Column(name = "end_time")
    private Timestamp end;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User booker;
    @Column
    private BookingStatus status;
}
