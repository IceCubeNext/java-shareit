package ru.practicum.shareit.exception;

import lombok.Generated;

@Generated
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
