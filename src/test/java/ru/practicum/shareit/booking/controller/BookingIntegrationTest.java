package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private UserDto userDto;
    private UserDto userDto2;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");

        userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("User2");
        userDto2.setEmail("mail2@mail.ru");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Дрель 'DeWalt' в отличном состоянии");
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);
    }

    @Test
    public void getEmptyUserBookings() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getEmptyOwnerBookings() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUserBookingsUser2() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingCreateDto.setItemId(1L);

        mockMvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mapper.registerModule(new JavaTimeModule());
        mockMvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", "2")
                                .content(mapper.writeValueAsString(bookingCreateDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(userDto2.getId()), Long.class));
    }
}
