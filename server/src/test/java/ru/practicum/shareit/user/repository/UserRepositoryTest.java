package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
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

        assertNull(user.getId());
        em.persist(user);
        assertNotNull(user.getId());
    }

    @Test
    public void duplicateEmail() {
        User user = new User();
        user.setName("User");
        user.setEmail("mail@mail.ru");
        em.persist(user);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("mail@mail.ru");

        assertThrows(PersistenceException.class, () -> em.persist(user2));
    }
}