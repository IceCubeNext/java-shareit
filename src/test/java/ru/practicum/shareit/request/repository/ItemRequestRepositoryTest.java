package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository repository;
    private User user;
    private User user2;
    private Request request;
    private Request request2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("mail2@mail.ru");

        request = new Request();
        request.setRequestor(user);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.now());

        request2 = new Request();
        request2.setRequestor(user2);
        request2.setDescription("Нужна отвертка");
        request2.setCreated(LocalDateTime.now().plusHours(1));

        em.persist(user);
        em.persist(user2);
        em.persist(request);
        em.persist(request2);
    }

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void generateIdTest() {
        User user = new User();
        user.setName("newUser");
        user.setEmail("newmail@mail.ru");
        em.persist(user);

        Request request = new Request();
        request.setDescription("Нужна пила");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);

        assertNull(request.getId());
        em.persist(request);
        assertNotNull(request.getId());
    }

    @Test
    public void findAllByRequestorOrderByCreatedDescTest() {
        List<Request> actual = repository.findAllByRequestorOrderByCreatedDesc(user);
        assertEquals(1, actual.size());
        assertEquals(request, actual.get(0));

        actual = repository.findAllByRequestorOrderByCreatedDesc(user2);
        assertEquals(1, actual.size());
        assertEquals(request2, actual.get(0));

        request2.setRequestor(user);
        actual = repository.findAllByRequestorOrderByCreatedDesc(user);
        assertEquals(2, actual.size());
        assertEquals(request2, actual.get(0));
        assertEquals(request, actual.get(1));
    }

    @Test
    public void findAllByRequestorNotOrderByCreatedDescTest() {
        PageRequest page = PageRequest.of(0, 10);
        Page<Request> actual = repository.findAllByRequestorNotOrderByCreatedDesc(user, page);
        assertEquals(1, actual.getContent().size());
        assertEquals(request2, actual.getContent().get(0));

        actual = repository.findAllByRequestorNotOrderByCreatedDesc(user2, page);
        assertEquals(1, actual.getContent().size());
        assertEquals(request, actual.getContent().get(0));

        request2.setRequestor(user);
        actual = repository.findAllByRequestorNotOrderByCreatedDesc(user2, page);
        assertEquals(2, actual.getContent().size());
        assertEquals(request2, actual.getContent().get(0));
        assertEquals(request, actual.getContent().get(1));
    }
}