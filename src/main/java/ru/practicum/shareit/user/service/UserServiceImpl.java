package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        checkUser(id);
        return userRepository.getUserById(id);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        checkUser(user.getId());
        return userRepository.updateUser(user);
    }

    @Override
    public User deleteUser(Long id) {
        checkUser(id);
        return userRepository.deleteUser(id);
    }

    private void checkUser(Long id) {
        if (!userRepository.containsUser(id)) {
            throw new NotFoundException(String.format("User with id=%d not found", id));
        }
    }
}
