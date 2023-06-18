package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void generateIdTest() {
        User user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");
        em.persist(user);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(user);
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
}