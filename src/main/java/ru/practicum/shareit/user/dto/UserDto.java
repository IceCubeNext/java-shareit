package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Name should not be empty")
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Email should not be empty")
    @Email(message = "Email incorrect")
    private String email;
}
