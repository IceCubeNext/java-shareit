package ru.practicum.shareit.user.utility;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    @Test
    public void mapToUserTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");

        User user = UserMapper.mapToUser(userDto);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void mapToUserDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        UserDto userDto = UserMapper.mapToUserDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}