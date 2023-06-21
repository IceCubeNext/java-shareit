package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    private User user;
    private User user2;
    private Item item;
    private Item item2;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("mail2@mail.ru");

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(user);

        item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Отвертка крестовая");
        item2.setAvailable(true);
        item2.setOwner(user);

        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(item2);
    }

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void generateIdTest() {
        User user = new User();
        user.setName("New User");
        user.setEmail("newmail@mail.ru");

        Item item = new Item();
        item.setName("Дрель новая");
        item.setDescription("Новая дрель рабочая");
        item.setAvailable(true);
        item.setOwner(user);

        em.persist(user);
        assertNull(item.getId());
        em.persist(item);
        assertNotNull(item.getId());
    }

    @Test
    public void findItemTest() {
        PageRequest page = PageRequest.of(0, 10);

        Page<Item> actual = itemRepository.findText("ДРЕЛЬ", page);
        assertEquals(1, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());

        actual = itemRepository.findText("дрель", page);
        assertEquals(1, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());

        actual = itemRepository.findText("дРЕль", page);
        assertEquals(1, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());

        actual = itemRepository.findText("р", page);
        assertEquals(2, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());
        assertEquals(item2.getName(), actual.getContent().get(1).getName());
    }

    @Test
    public void findAllByRequestOrderByRequestCreatedDescTest() {
        Request request = new Request();
        request.setRequestor(user2);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.now());
        em.persist(request);

        List<Item> actual = itemRepository.findAllByRequestOrderByRequestCreatedDesc(request);
        assertEquals(0, actual.size());

        item.setRequest(request);
        actual = itemRepository.findAllByRequestOrderByRequestCreatedDesc(request);
        assertEquals(1, actual.size());
        assertEquals(item.getName(), actual.get(0).getName());
    }

    @Test
    public void findAllByOwnerOrderByIdTest() {
        PageRequest page = PageRequest.of(0, 10);
        Page<Item> actual = itemRepository.findAllByOwnerOrderById(user, page);
        assertEquals(2, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());
        assertEquals(item2.getName(), actual.getContent().get(1).getName());
    }

    @Test
    public void findByRequestInTest() {
        Request request = new Request();
        request.setRequestor(user2);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.now());
        em.persist(request);

        List<Item> actual = itemRepository.findByRequestIn(List.of(request), Sort.by(DESC, "id"));
        assertEquals(0, actual.size());

        item.setRequest(request);
        actual = itemRepository.findAllByRequestOrderByRequestCreatedDesc(request);
        assertEquals(1, actual.size());
        assertEquals(item.getName(), actual.get(0).getName());
    }
}