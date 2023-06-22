package ru.practicum.shareit.validation;

import lombok.Generated;
import lombok.experimental.UtilityClass;

@Generated
@UtilityClass
public class PageValidation {
    public void validatePageParameters(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new IllegalArgumentException("Page parameters incorrect");
        }
    }
}
