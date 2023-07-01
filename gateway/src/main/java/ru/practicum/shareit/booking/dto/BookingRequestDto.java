package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.validation.Marker;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
@StartBeforeEndDateValid(groups = {Marker.OnCreate.class})
public class BookingRequestDto {
    @FutureOrPresent(groups = {Marker.OnCreate.class}, message = "Start of booking should not be in the past")
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull(groups = {Marker.OnCreate.class}, message = "Item of booking should not be null")
    private Long itemId;
}