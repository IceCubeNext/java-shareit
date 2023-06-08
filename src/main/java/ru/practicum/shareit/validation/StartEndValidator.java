package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingCreateDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingCreateDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingCreateDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
