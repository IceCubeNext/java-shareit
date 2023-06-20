package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentCreateDto;
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
class ItemIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private UserDto userDto;
    private UserDto userDto2;
    private ItemDto itemDto;
    private ItemDto itemDto2;

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

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Отвертка");
        itemDto2.setDescription("Отвертка крестовая");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(2L);
    }

    @Test
    public void addItemTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        getItemById(1L, userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель")));
    }

    @Test
    public void addItemWithoutNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setName(null);
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemWithoutDescriptionTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setDescription(null);
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemEmptyNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setName("");
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemEmptyDescriptionTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setDescription("");
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemBlankNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setName("       ");
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemBlankDescriptionTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setDescription("     ");
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemWithoutAvailableTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        itemDto.setAvailable(null);
        postItem(userDto.getId(), itemDto).andExpect(status().isBadRequest());
    }

    @Test
    public void addItemWithoutHeaderTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        mockMvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAbsentItemByIdTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        getItemById(2L, userDto.getId()).andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByUserIdTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto2.getId(), itemDto2).andExpect(status().isOk());

        getItemsByUserId(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Дрель"));

        getItemsByUserId(userDto2.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Отвертка"));
    }

    @Test
    public void getItemsByAbsentUserIdTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        getItemsByUserId(userDto2.getId()).andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByIdWithoutHeaderTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        mockMvc.perform(
                        get("/items")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItemsTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());

        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto2.getId(), itemDto2).andExpect(status().isOk());
        mockMvc.perform(
                        get("/items/search?text=ДРЕ")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Дрель"));

        mockMvc.perform(
                        get("/items/search?text=р")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("Дрель"))
                .andExpect(jsonPath("$.[1].name").value("Отвертка"));

        mockMvc.perform(
                        get("/items/search?text=")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void updateItemNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());

        itemDto.setName("Уже не дрель");
        patchItem(itemDto.getId(), userDto.getId(), itemDto).andExpect(status().isOk());
        getItemById(itemDto.getId(), userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Уже не дрель"));
    }

    @Test
    public void updateItemDescriptionTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());

        itemDto.setDescription("Не новая дрель");
        patchItem(itemDto.getId(), userDto.getId(), itemDto).andExpect(status().isOk());
        getItemById(itemDto.getId(), userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Не новая дрель"));
    }

    @Test
    public void updateItemAvailableTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());

        itemDto.setAvailable(false);
        patchItem(itemDto.getId(), userDto.getId(), itemDto).andExpect(status().isOk());
        getItemById(itemDto.getId(), userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    public void updateAbsentItemTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        patchItem(2L, userDto.getId(), itemDto).andExpect(status().isNotFound());
    }

    @Test
    public void updateAbsentUserTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        patchItem(itemDto.getId(), 2L, itemDto).andExpect(status().isNotFound());
    }

    @Test
    public void deleteItemTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        deleteItem(itemDto.getId(), userDto.getId()).andExpect(status().isOk());
        getItemById(itemDto.getId(), userDto.getId()).andExpect(status().isNotFound());
    }

    @Test
    public void deleteAbsentItemTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        deleteItem(99L, userDto.getId()).andExpect(status().isNotFound());
    }

    @Test
    public void addCommentUserNotUseItemTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());
        CommentCreateDto comment = new CommentCreateDto("Новый комментарий");
        mockMvc.perform(
                post("/items/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        getItemById(itemDto.getId(), userDto.getId()).andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(0)));

    }

    @Test
    public void addCommentUserNotFound() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusSeconds(3));
        bookingCreateDto.setEnd(LocalDateTime.now().plusSeconds(5));
        bookingCreateDto.setItemId(itemDto.getId());

        mapper.registerModule(new JavaTimeModule());
        mockMvc.perform(
                post("/bookings")
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mockMvc.perform(
                patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "99")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void addCommentItemNotFound() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postItem(userDto.getId(), itemDto).andExpect(status().isOk());

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusSeconds(3));
        bookingCreateDto.setEnd(LocalDateTime.now().plusSeconds(5));
        bookingCreateDto.setItemId(itemDto.getId());

        mapper.registerModule(new JavaTimeModule());
        mockMvc.perform(
                post("/bookings")
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mockMvc.perform(
                patch("/bookings/99?approved=true")
                        .header("X-Sharer-User-Id", "1")
        ).andExpect(status().isNotFound());
    }

    private ResultActions getItemById(Long itemId, Long userId) throws Exception {
        return mockMvc.perform(
                get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
        );
    }

    private ResultActions getItemsByUserId(Long userId) throws Exception {
        return mockMvc.perform(
                get("/items")
                        .header("X-Sharer-User-Id", userId)
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

    private ResultActions patchItem(Long itemId, Long userId, ItemDto itemDto) throws Exception {
        return mockMvc.perform(
                patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions deleteItem(Long itemId, Long userId) throws Exception {
        return mockMvc.perform(
                delete("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
        );
    }
}