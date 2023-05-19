package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final Set<String> logins = new HashSet<>();
    private Long id = 0L;

    @Override
    public Boolean containsUser(Long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", id));
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        if (logins.contains(user.getName())) {
            throw new UserAlreadyExistsException(String.format("Attempt to save user with duplicate name %s", user.getName()));
        } else if (emails.contains(user.getEmail())) {
            throw new UserAlreadyExistsException(String.format("Attempt to save user with duplicate email %s", user.getName()));
        }
        Long id = getNewId();
        user.setId(id);
        users.put(id, user);
        logins.add(user.getName());
        emails.add(user.getEmail());
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        if (users.containsKey(id)) {
            User usr = users.get(id);
            if (user.getName() != null && !usr.getName().equals(user.getName())) {
                if (logins.contains(user.getName())) {
                    throw new UserAlreadyExistsException(String.format("Attempt to save user with duplicate name %s", user.getName()));
                }
                logins.remove(usr.getName());
                logins.add(user.getName());
                users.get(id).setName(user.getName());
            }
            if (user.getEmail() != null && !usr.getEmail().equals(user.getEmail())) {
                if (emails.contains(user.getEmail())) {
                    throw new UserAlreadyExistsException(String.format("Attempt to save user with duplicate email %s", user.getName()));
                }
                emails.remove(usr.getEmail());
                emails.add(user.getEmail());
                users.get(id).setEmail(user.getEmail());
            }
            return users.get(id);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", id));
        }
    }

    @Override
    public User deleteUser(Long id) {
        if (users.containsKey(id)) {
            logins.remove(users.get(id).getName());
            emails.remove(users.get(id).getEmail());
            return users.remove(id);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", id));
        }
    }

    private Long getNewId() {
        return ++this.id;
    }
}
