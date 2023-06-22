package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private List<CommentDto> comments;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}
