package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void generateIdTest() {
        User user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(user);

        em.persist(user);
        assertNull(item.getId());
        em.persist(item);
        assertNotNull(item.getId());
    }

    @Test
    public void findItem() {
        User user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");
        em.persist(user);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(user);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Отвертка крестовая");
        item2.setAvailable(true);
        item2.setOwner(user);

        em.persist(item);
        em.persist(item2);
        PageRequest page = PageRequest.of(0, 10);

        Page<Item> actual = itemRepository.findText("ДРЕЛЬ", page);
        assertEquals(1, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());

        actual = itemRepository.findText("р", page);
        assertEquals(2, actual.getContent().size());
        assertEquals(item.getName(), actual.getContent().get(0).getName());
        assertEquals(item2.getName(), actual.getContent().get(1).getName());
    }
}