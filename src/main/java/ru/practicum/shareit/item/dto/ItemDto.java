package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
public class ItemDto {
    @PositiveOrZero(message = "Id should be positive or zero")
    private Long id;
    @NotEmpty(message = "Name should not be empty")
    private String name;
    @NotEmpty(message = "Description should not be empty")
    private String description;
    private Boolean available;
    @PositiveOrZero(message = "Owner id should be positive or zero")
    private Long owner;
    private ItemRequest request;
}
