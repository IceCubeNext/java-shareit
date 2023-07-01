package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    User getUser(Long id);

    List<UserDto> getUsers();

    UserDto addUser(UserDto user);

    UserDto updateUser(UserDto user, Long id);

    void deleteUser(Long id);
}
