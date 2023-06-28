package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class ItemRequestDto {
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Name should not be empty")
    @Size(max = 255, groups = {Marker.OnCreate.class}, message = "Name should be less than 2000 symbols")
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Description should not be empty")
    @Size(max = 2000, groups = {Marker.OnCreate.class}, message = "Text should be less than 2000 symbols")
    private String description;
    @NotNull(groups = {Marker.OnCreate.class}, message = "Available should not be null")
    private Boolean available;
    private Long owner;
    private Long requestId;
}
