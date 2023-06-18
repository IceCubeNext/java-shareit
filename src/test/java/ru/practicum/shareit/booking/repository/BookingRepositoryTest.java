package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(user);

        em.persist(user);
        em.persist(item);
    }

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void generateIdTest() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());

        assertNull(booking.getId());
        em.persist(booking);
        assertNotNull(booking.getId());
    }

    @Test
    public void findLastBooking() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now());

        Booking booking2 = new Booking();
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().plusHours(1));
        booking2.setEnd(LocalDateTime.now().plusHours(2));

        em.persist(booking);
        em.persist(booking2);
        Booking actual = bookingRepository.findLastBooking(item.getId());
        assertNull(actual);
        booking.setStatus(BookingStatus.APPROVED);
        booking2.setStatus(BookingStatus.APPROVED);
        actual = bookingRepository.findLastBooking(item.getId());
        assertEquals(booking.getId(), actual.getId());
    }

    @Test
    public void findNextBooking() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now());

        Booking booking2 = new Booking();
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().plusHours(1));
        booking2.setEnd(LocalDateTime.now().plusHours(2));

        em.persist(booking);
        em.persist(booking2);
        Booking actual = bookingRepository.findNextBooking(item.getId());
        assertNull(actual);
        booking.setStatus(BookingStatus.APPROVED);
        booking2.setStatus(BookingStatus.APPROVED);
        actual = bookingRepository.findNextBooking(item.getId());
        assertEquals(booking2.getId(), actual.getId());
    }
}