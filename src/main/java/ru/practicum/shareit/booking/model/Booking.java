package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

@Data
@Builder
public class Booking {
    @PositiveOrZero(message = "Id should be positive or zero")
    private Long id;
    @NotNull(message = "Date of start should not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date start;
    @NotNull(message = "Date of end should not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date end;
    @NotNull(message = "Item of booking should not be null")
    private Item item;
    @NotNull(message = "Booker should not be null")
    private User booker;
    @NotNull(message = "Booking status should not be null")
    private BookStatus status;
}
