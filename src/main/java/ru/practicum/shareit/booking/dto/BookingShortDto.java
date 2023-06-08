package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {
    private Long id;
    private Long itemId;
    private Long bookerId;
}
