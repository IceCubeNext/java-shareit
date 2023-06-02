package ru.practicum.shareit.user.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {
    public User mapToUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserDto> mapToUserDto(List<User> users) {
        return users.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }
}
