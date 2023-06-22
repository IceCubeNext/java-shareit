package ru.practicum.shareit.exception;

import lombok.Generated;

@Generated
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}