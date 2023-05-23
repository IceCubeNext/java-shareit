package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utility.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Marker;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.mapToUserDto(user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> users = userService.getUsers();
        return UserMapper.mapToUsersDto(users);
    }

    @PostMapping
    public UserDto addUser(@Validated({Marker.OnCreate.class}) @RequestBody UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userService.addUser(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated({Marker.OnUpdate.class}) @RequestBody UserDto userDto,
                              @PathVariable Long id) {
        User user = UserMapper.mapToUser(userDto);
        user.setId(id);
        return UserMapper.mapToUserDto(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable Long id) {
        User user = userService.deleteUser(id);
        return UserMapper.mapToUserDto(user);
    }
}
