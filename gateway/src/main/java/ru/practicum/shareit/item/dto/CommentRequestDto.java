package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class CommentRequestDto {
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Text should not be empty")
    @Size(max = 2000, groups = {Marker.OnCreate.class}, message = "Text should be less than 2000 symbols")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
