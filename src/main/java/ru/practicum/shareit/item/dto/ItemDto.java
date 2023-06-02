package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Name should not be empty")
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Description should not be empty")
    private String description;
    @NotNull(groups = {Marker.OnCreate.class}, message = "Available should not be null")
    private Boolean available;
    private Long owner;
}
