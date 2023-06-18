package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    public void serializeInCorrectFormat() throws IOException {
        BookingDto booking = new BookingDto();
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(new BookingDto.Booker(1L, "User"));
        booking.setItem(new BookingDto.Item(1L, "Дрель"));
        booking.setStart(LocalDateTime.of(2023, 6, 18, 12, 0));
        booking.setEnd(LocalDateTime.of(2023, 6, 19, 12, 0));

        JsonContent<BookingDto> actual = json.write(booking);

        assertThat(actual).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-18T12:00:00");
        assertThat(actual).extractingJsonPathStringValue("$.end").isEqualTo("2023-06-19T12:00:00");

    }
}