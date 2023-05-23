package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ItemController itemController;
    @Autowired
    UserController userController;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void addItemTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Дрель")
                .description("Делать ремонт")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void addItemWithoutNameTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .description("делать ремонт")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addItemWithoutDescriptionTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Дрель")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addItemWithoutAvailableTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Дрель")
                .description("делать ремонт")
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addItemWithoutHeaderTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Дрель")
                .description("делать ремонт")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getItemByIdTest() throws Exception {
        addUser("User2", "mail2@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        item.setId(1L);
        item.setOwner(1L);
        String jsonExpected = mapper.writeValueAsString(item);
        MvcResult result = mockMvc.perform(
                        get("/items/1")
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(jsonExpected, result.getResponse().getContentAsString());
    }

    @Test
    public void getAbsentItemByIdTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/items/2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemByIdAbsentUserTest() throws Exception {
        addUser("User2", "mail2@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "2")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByUserIdTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        ItemDto item2 = ItemDto.builder()
                .name("Car")
                .description("Sharing")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        String json2 = mapper.writeValueAsString(item2);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("Camera"))
                .andExpect(jsonPath("$.[1].name").value("Car"));
    }

    @Test
    public void getItemsByAbsentUserIdTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        ItemDto item2 = ItemDto.builder()
                .name("Car")
                .description("Sharing")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        String json2 = mapper.writeValueAsString(item2);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByIdWithoutHeaderTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        ItemDto item2 = ItemDto.builder()
                .name("Car")
                .description("Sharing")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        String json2 = mapper.writeValueAsString(item2);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/items")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void searchItemsTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        ItemDto item2 = ItemDto.builder()
                .name("Car")
                .description("Sharing")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        String json2 = mapper.writeValueAsString(item2);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/items/search?text=cA")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("Camera"))
                .andExpect(jsonPath("$.[1].name").value("Car"));

        mockMvc.perform(
                        get("/items/search?text=Cam")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Camera"));

        mockMvc.perform(
                        get("/items/search?text=")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void updateItemNameTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        ItemDto newItem = ItemDto.builder()
                .name("New Camera")
                .owner(1L)
                .description("Make photo")
                .available(true)
                .build();
        String newJson = mapper.writeValueAsString(newItem);
        mockMvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", "1")
                                .content(newJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        newItem.setId(1L);
        String jsonExpected = mapper.writeValueAsString(newItem);
        MvcResult result = mockMvc.perform(
                        get("/items/1")
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(jsonExpected, result.getResponse().getContentAsString());
    }

    @Test
    public void updateItemDescriptionTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        ItemDto newItem = ItemDto.builder()
                .name("Camera")
                .owner(1L)
                .description("Make photography")
                .available(true)
                .build();
        String newJson = mapper.writeValueAsString(newItem);
        mockMvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", "1")
                                .content(newJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        newItem.setId(1L);
        String jsonExpected = mapper.writeValueAsString(newItem);
        MvcResult result = mockMvc.perform(
                        get("/items/1")
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(jsonExpected, result.getResponse().getContentAsString());
    }

    @Test
    public void updateItemAvailableTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        ItemDto newItem = ItemDto.builder()
                .name("Camera")
                .owner(1L)
                .description("Make photo")
                .available(false)
                .build();
        String newJson = mapper.writeValueAsString(newItem);
        mockMvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", "1")
                                .content(newJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        newItem.setId(1L);
        String jsonExpected = mapper.writeValueAsString(newItem);
        MvcResult result = mockMvc.perform(
                        get("/items/1")
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(jsonExpected, result.getResponse().getContentAsString());
    }

    @Test
    public void updateAbsentItemTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        ItemDto newItem = ItemDto.builder()
                .name("Camera")
                .owner(1L)
                .description("Make photo")
                .available(false)
                .build();
        String newJson = mapper.writeValueAsString(newItem);
        mockMvc.perform(
                        patch("/items/2")
                                .header("X-Sharer-User-Id", "1")
                                .content(newJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateAbsentUserTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        ItemDto newItem = ItemDto.builder()
                .name("Camera")
                .owner(1L)
                .description("Make photo")
                .available(false)
                .build();
        String newJson = mapper.writeValueAsString(newItem);
        mockMvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", "2")
                                .content(newJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteItemTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        ItemDto item2 = ItemDto.builder()
                .name("Car")
                .description("Sharing")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        String json2 = mapper.writeValueAsString(item2);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        delete("/items/1")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Car"));
    }

    @Test
    public void deleteAbsentItemTest() throws Exception {
        addUser("User", "mail@mail.ru");
        ItemDto item = ItemDto.builder()
                .name("Camera")
                .description("Make photo")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(item);
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/items/2")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isNotFound());
    }

    private void addUser(String name, String email) throws Exception {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}