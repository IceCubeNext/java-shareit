package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {
    @NotNull(groups = {Marker.OnCreate.class}, message = "Start of booking should not be null")
    @FutureOrPresent(groups = {Marker.OnCreate.class}, message = "Start of booking should not be in the past")
    private LocalDateTime start;
    @NotNull(groups = {Marker.OnCreate.class}, message = "End of booking should not be null")
    @FutureOrPresent(groups = {Marker.OnCreate.class}, message = "End of booking should not be in the past")
    private LocalDateTime end;
    @NotNull(groups = {Marker.OnCreate.class}, message = "Item of booking should not be null")
    private Long itemId;
}