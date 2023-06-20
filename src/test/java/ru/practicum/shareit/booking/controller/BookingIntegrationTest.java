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
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private BookingCreateDto bookingCreateDto;

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

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingCreateDto.setItemId(1L);

        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void getBookingById() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        postBooking(userDto2.getId(), bookingCreateDto).andExpect(status().isOk());
        getBookingByUserId(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class));
    }

    @Test
    public void addBookingWrongStartEnd() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateDto.setEnd(LocalDateTime.now().minusHours(1));

        postBooking(userDto2.getId(), bookingCreateDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getEmptyUserBookings() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        getUserBookings(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getEmptyOwnerBookings() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        getOwnerBookings(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUserBookings() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        postBooking(userDto2.getId(), bookingCreateDto).andExpect(status().isOk());

        getUserBookings(userDto2.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(userDto2.getId()), Long.class));
    }

    @Test
    public void getOwnerBookings() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        postBooking(userDto2.getId(), bookingCreateDto).andExpect(status().isOk());

        getOwnerBookings(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(userDto2.getId()), Long.class));
    }

    @Test
    public void approveBookingAccept() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        postBooking(userDto2.getId(), bookingCreateDto).andExpect(status().isOk());

        mockMvc.perform(
                patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "1")
        );

        getOwnerBookings(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is("APPROVED")));
    }

    @Test
    public void approveBookingRejected() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        postBooking(userDto2.getId(), bookingCreateDto).andExpect(status().isOk());

        mockMvc.perform(
                patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", "1")
        );

        getOwnerBookings(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is("REJECTED")));
    }

    private ResultActions getBookingByUserId(Long userId) throws Exception {
        return  mockMvc.perform(
                get("/bookings/" + userId.toString())
                        .header("X-Sharer-User-Id", userId)
        );
    }

    private ResultActions getOwnerBookings(Long userId) throws Exception {
        return  mockMvc.perform(
                get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
        );
    }

    private ResultActions getUserBookings(Long userId) throws Exception {
        return  mockMvc.perform(
                get("/bookings")
                        .header("X-Sharer-User-Id", userId)
        );
    }

    private ResultActions postBooking(Long userId, BookingCreateDto bookingCreateDto) throws Exception {
        return  mockMvc.perform(
                post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions postUser(UserDto userDto) throws Exception {
        return mockMvc.perform(
               post("/users")
                       .content(mapper.writeValueAsString(userDto))
                       .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions postItem(Long userId, ItemDto itemDto) throws Exception {
        return mockMvc.perform(
                post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
