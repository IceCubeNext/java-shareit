package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.utility.UserMapper;

import java.util.List;
import java.util.Objects;

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
        checkEmail(userDto);
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = getUser(id);
        if (StringUtils.hasLength(userDto.getName()) && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
        }
        if (StringUtils.hasLength(userDto.getEmail()) && !userDto.getEmail().equals(user.getEmail())) {
            checkEmail(userDto);
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    private void checkEmail(UserDto userDto) {
        if (!StringUtils.hasLength(userDto.getEmail())) return;
        User user = userRepository.getUserByEmail(userDto.getEmail());
        if (user != null && !Objects.equals(user.getId(), userDto.getId())) {
            throw new UserAlreadyExistsException(String.format("Email %s already exists", userDto.getEmail()));
        }
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", id)));
    }
}
