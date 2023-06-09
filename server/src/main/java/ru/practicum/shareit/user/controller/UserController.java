package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable Long id) {
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
