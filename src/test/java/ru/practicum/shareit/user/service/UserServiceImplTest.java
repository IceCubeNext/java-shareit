package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.utility.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");
    }

    @Test
    public void getUserById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));

        UserDto actual = userService.getUserById(user.getId());

        assertEquals(user.getId(), actual.getId());
        assertEquals(user.getEmail(), actual.getEmail());
        assertEquals(user.getName(), actual.getName());
    }

    @Test
    public void getUserByIdUserNotFound() {
        when(userRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    public void getUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> actual = userService.getUsers();

        assertEquals(1, actual.size());
        assertEquals(user.getId(), actual.get(0).getId());
        assertEquals(user.getName(), actual.get(0).getName());
        assertEquals(user.getEmail(), actual.get(0).getEmail());
    }

    @Test
    public void updateUserNotFound() {
        when(userRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.updateUser(UserMapper.mapToUserDto(user), 99L));
    }

    @Test
    public void updateUserName() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("NewUser");
        newUser.setEmail("mail@mail.ru");

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));

        UserDto actual = userService.updateUser(UserMapper.mapToUserDto(newUser), user.getId());

        assertEquals(newUser.getId(), actual.getId());
        assertEquals(newUser.getEmail(), actual.getEmail());
        assertEquals(newUser.getName(), actual.getName());
    }

    @Test
    public void updateUserEmail() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("User");
        newUser.setEmail("mail_new@mail.ru");

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));

        UserDto actual = userService.updateUser(UserMapper.mapToUserDto(newUser), user.getId());

        assertEquals(newUser.getId(), actual.getId());
        assertEquals(newUser.getEmail(), actual.getEmail());
        assertEquals(newUser.getName(), actual.getName());
    }
}