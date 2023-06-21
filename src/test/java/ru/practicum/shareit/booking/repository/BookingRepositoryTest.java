package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private Booking bookingTemp;
    private Booking bookingWaiting;
    private Booking bookingApprove;
    private Booking bookingRejected;
    private PageRequest page;

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

        bookingWaiting = new Booking();
        bookingWaiting.setStatus(BookingStatus.WAITING);
        bookingWaiting.setBooker(user);
        bookingWaiting.setItem(item);
        bookingWaiting.setStart(LocalDateTime.now().plusMinutes(7));
        bookingWaiting.setEnd(LocalDateTime.now().plusHours(1));

        bookingApprove = new Booking();
        bookingApprove.setStatus(BookingStatus.APPROVED);
        bookingApprove.setBooker(user);
        bookingApprove.setItem(item);
        bookingApprove.setStart(LocalDateTime.now().plusMinutes(5));
        bookingApprove.setEnd(LocalDateTime.now().plusHours(1));

        bookingRejected = new Booking();
        bookingRejected.setStatus(BookingStatus.REJECTED);
        bookingRejected.setBooker(user);
        bookingRejected.setItem(item);
        bookingRejected.setStart(LocalDateTime.now().plusMinutes(3));
        bookingRejected.setEnd(LocalDateTime.now().plusHours(1));

        bookingTemp = new Booking();
        bookingTemp.setBooker(user);
        bookingTemp.setItem(item);
        bookingTemp.setStart(LocalDateTime.now());
        bookingTemp.setEnd(LocalDateTime.now().plusHours(1));

        em.persist(bookingWaiting);
        em.persist(bookingApprove);
        em.persist(bookingRejected);
        em.persist(bookingTemp);
        page = PageRequest.of(0, 10);
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
        bookingApprove.setEnd(LocalDateTime.now().minusHours(1));
        bookingApprove.setStart(LocalDateTime.now().minusHours(2));
        Booking actual = bookingRepository.findLastBooking(item.getId());
        assertEquals(bookingApprove.getId(), actual.getId());
    }

    @Test
    public void findNextBooking() {
        bookingApprove.setEnd(LocalDateTime.now().minusHours(1));
        bookingApprove.setStart(LocalDateTime.now().minusHours(2));

        bookingTemp.setStatus(BookingStatus.WAITING);
        bookingTemp.setBooker(user);
        bookingTemp.setItem(item);
        bookingTemp.setStart(LocalDateTime.now().plusHours(1));
        bookingTemp.setEnd(LocalDateTime.now().plusHours(2));

        Booking actual = bookingRepository.findNextBooking(item.getId());
        assertNull(actual);
        bookingTemp.setStatus(BookingStatus.APPROVED);
        actual = bookingRepository.findNextBooking(item.getId());
        assertEquals(bookingTemp.getId(), actual.getId());
    }

    @Test
    public void findCompletedBookingStatusChange() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        em.persist(booking);
        Booking actual = bookingRepository.findCompletedBooking(item.getId(), user.getId());
        assertNull(actual);
        booking.setStatus(BookingStatus.APPROVED);
        actual = bookingRepository.findCompletedBooking(item.getId(), user.getId());
        assertNotNull(actual);
        assertEquals(booking.getId(), actual.getId());
    }

    @Test
    public void findCompletedBookingTimeChange() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        em.persist(booking);
        Booking actual = bookingRepository.findCompletedBooking(item.getId(), user.getId());
        assertNull(actual);

        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        actual = bookingRepository.findCompletedBooking(item.getId(), user.getId());
        assertNull(actual);

        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        actual = bookingRepository.findCompletedBooking(item.getId(), user.getId());
        assertNull(actual);

        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        actual = bookingRepository.findCompletedBooking(item.getId(), user.getId());
        assertNotNull(actual);
        assertEquals(booking.getId(), actual.getId());
    }

    @Test
    public void findAllByBookerIdAndStatusOrderByStartDescTest() {
        Page<Booking> actual = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, page);
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));

        bookingTemp.setStatus(BookingStatus.WAITING);
        actual = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, page);
        assertNotNull(actual);
        assertEquals(2, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingTemp, actual.getContent().get(1));
    }

    @Test
    public void findAllByBookerIdOrderByStartDescTest() {
        Page<Booking> actual = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), page);
        assertNotNull(actual);
        assertEquals(4, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingApprove, actual.getContent().get(1));
        assertEquals(bookingRejected, actual.getContent().get(2));
        assertEquals(bookingTemp, actual.getContent().get(3));
    }

    @Test
    public void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(10);
        Page<Booking> actual = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(), time, time, page);
        assertNotNull(actual);
        assertEquals(4, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingApprove, actual.getContent().get(1));
        assertEquals(bookingRejected, actual.getContent().get(2));
        assertEquals(bookingTemp, actual.getContent().get(3));
    }

    @Test
    public void findAllByBookerIdAndEndBeforeOrderByStartDescTest() {
        LocalDateTime time = LocalDateTime.now();
        Page<Booking> actual = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(0, actual.getContent().size());

        bookingTemp.setStart(LocalDateTime.now().minusHours(2));
        bookingTemp.setEnd(LocalDateTime.now().minusHours(1));
        actual = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(bookingTemp, actual.getContent().get(0));
    }

    @Test
    public void findAllByBookerIdAndStartAfterOrderByStartDescTest() {
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        Page<Booking> actual = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(0, actual.getContent().size());

        time = LocalDateTime.now().minusHours(1);
        actual = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(4, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingApprove, actual.getContent().get(1));
        assertEquals(bookingRejected, actual.getContent().get(2));
        assertEquals(bookingTemp, actual.getContent().get(3));
    }

    // owner

    @Test
    public void findAllByItemOwnerIdAndStatusOrderByStartDescTest() {
        Page<Booking> actual = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, page);
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));

        bookingTemp.setStatus(BookingStatus.WAITING);
        actual = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, page);
        assertNotNull(actual);
        assertEquals(2, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingTemp, actual.getContent().get(1));
    }

    @Test
    public void findAllByItemOwnerIdOrderByStartDescTest() {
        Page<Booking> actual = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId(), page);
        assertNotNull(actual);
        assertEquals(4, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingApprove, actual.getContent().get(1));
        assertEquals(bookingRejected, actual.getContent().get(2));
        assertEquals(bookingTemp, actual.getContent().get(3));
    }

    @Test
    public void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(10);
        Page<Booking> actual = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(), time, time, page);
        assertNotNull(actual);
        assertEquals(4, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingApprove, actual.getContent().get(1));
        assertEquals(bookingRejected, actual.getContent().get(2));
        assertEquals(bookingTemp, actual.getContent().get(3));
    }

    @Test
    public void findAllByItemOwnerIdAndEndBeforeOrderByStartDescTest() {
        LocalDateTime time = LocalDateTime.now();
        Page<Booking> actual = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(0, actual.getContent().size());

        bookingTemp.setStart(LocalDateTime.now().minusHours(2));
        bookingTemp.setEnd(LocalDateTime.now().minusHours(1));
        actual = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(bookingTemp, actual.getContent().get(0));
    }

    @Test
    public void findAllByItemOwnerIdAndStartAfterOrderByStartDescTest() {
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        Page<Booking> actual = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(0, actual.getContent().size());

        time = LocalDateTime.now().minusHours(1);
        actual = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(user.getId(), time, page);
        assertNotNull(actual);
        assertEquals(4, actual.getContent().size());
        assertEquals(bookingWaiting, actual.getContent().get(0));
        assertEquals(bookingApprove, actual.getContent().get(1));
        assertEquals(bookingRejected, actual.getContent().get(2));
        assertEquals(bookingTemp, actual.getContent().get(3));
    }
}