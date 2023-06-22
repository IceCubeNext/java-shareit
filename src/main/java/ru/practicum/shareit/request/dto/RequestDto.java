package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class RequestDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class}, message = "name should not be empty")
    @Size(max = 2000, groups = {Marker.OnCreate.class}, message = "Text should be less than 2000 symbols")
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private List<ItemDto> items;
}
