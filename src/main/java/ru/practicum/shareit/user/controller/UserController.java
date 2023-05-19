package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utility.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper = new UserMapper();

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        User user =  userService.getUserById(id);
        return userMapper.mapToUserDto(user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> users = userService.getUsers();
        return users.stream().map(userMapper::mapToUserDto).collect(Collectors.toList());
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        return userMapper.mapToUserDto(userService.addUser(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable Long id) {
        User user = userMapper.mapToUser(userDto);
        user.setId(id);
        return userMapper.mapToUserDto(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable Long id) {
        User user = userService.deleteUser(id);
        return userMapper.mapToUserDto(user);
    }
}
