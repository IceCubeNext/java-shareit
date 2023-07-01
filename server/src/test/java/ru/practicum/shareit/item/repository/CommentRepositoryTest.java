package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;
    private User user;
    private User user2;
    private Item item;
    private Item item2;

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
        item2.setOwner(user2);

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
        em.persist(item);

        Comment comment = new Comment();
        comment.setText("Отличная вещь");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        assertNull(comment.getId());
        em.persist(comment);
        assertNotNull(comment.getId());
    }

    @Test
    public void findAllByItemIdTest() {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setItem(item2);
        comment.setText("все отлично. рекомендую.");
        comment.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setAuthor(user2);
        comment2.setItem(item);
        comment2.setText("не понравилось.");
        comment2.setCreated(LocalDateTime.now());

        List<Comment> actual = commentRepository.findAllByItemId(item.getId());
        assertEquals(0, actual.size());
        em.persist(comment);
        em.persist(comment2);
        actual = commentRepository.findAllByItemId(item2.getId());
        assertEquals(1, actual.size());
        assertEquals(comment.getId(), actual.get(0).getId());

        actual = commentRepository.findAllByItemId(item.getId());
        assertEquals(1, actual.size());
        assertEquals(comment2.getId(), actual.get(0).getId());
    }

    @Test
    public void findByItemInTest() {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setItem(item2);
        comment.setText("все отлично. рекомендую.");
        comment.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setAuthor(user2);
        comment2.setItem(item);
        comment2.setText("не понравилось.");
        comment2.setCreated(LocalDateTime.now());

        List<Comment> actual = commentRepository.findByItemIn(List.of(item), Sort.by(DESC, "id"));
        assertEquals(0, actual.size());
        em.persist(comment);
        em.persist(comment2);
        actual = commentRepository.findByItemIn(List.of(item2), Sort.by(DESC, "id"));
        assertEquals(1, actual.size());
        assertEquals(comment.getId(), actual.get(0).getId());

        actual = commentRepository.findByItemIn(List.of(item), Sort.by(DESC, "id"));
        assertEquals(1, actual.size());
        assertEquals(comment2.getId(), actual.get(0).getId());
    }
}