package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long id = 0L;

    @Override
    public Boolean containsUser(Long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        checkEmail(user);
        Long id = getNewId();
        user.setId(id);
        users.put(id, user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        User usr = users.get(id);
        if (StringUtils.hasLength(user.getName()) && !usr.getName().equals(user.getName())) {
            users.get(id).setName(user.getName());
        }
        if (StringUtils.hasLength(user.getEmail()) && !usr.getEmail().equals(user.getEmail())) {
            checkEmail(user);
            emails.remove(usr.getEmail());
            emails.add(user.getEmail());
            users.get(id).setEmail(user.getEmail());
        }
        return usr;
    }

    @Override
    public User deleteUser(Long id) {
        emails.remove(users.get(id).getEmail());
        return users.remove(id);
    }

    private void checkEmail(User user) {
        if (emails.contains(user.getEmail())) {
            throw new UserAlreadyExistsException(String.format("Attempt to save user with duplicate email %s", user.getName()));
        }
    }

    private Long getNewId() {
        return ++this.id;
    }
}
