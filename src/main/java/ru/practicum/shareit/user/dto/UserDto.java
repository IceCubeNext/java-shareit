package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Name should not be empty")
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Email should not be empty")
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Email incorrect")
    private String email;
}
