package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.utility.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long id) {
        User user = getUser(id);
        return UserMapper.mapToUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = getUser(id);
        if (StringUtils.hasText(userDto.getName()) && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
        }
        if (StringUtils.hasText(userDto.getEmail()) && !userDto.getEmail().equals(user.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", id)));
    }
}
