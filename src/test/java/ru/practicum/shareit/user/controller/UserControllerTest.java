package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserController userController;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getEmptyUsers() throws Exception {
        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void addNormalUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void addUserWithIncorrectEmail() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyMail() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyName() throws Exception {
        UserDto user = UserDto.builder()
                .name("")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithExistsName() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        user = UserDto.builder()
                .name("User")
                .email("mail2@mail.ru")
                .build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    public void addUserWithExistsMail() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        user = UserDto.builder()
                .name("User2")
                .email("mail@mail.ru")
                .build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        UserDto user2 = UserDto.builder()
                .name("User2")
                .email("mail2@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        String json2 = mapper.writeValueAsString(user2);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/users")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        user.setId(1L);
        user2.setId(2L);
        String jsonExpected = mapper.writeValueAsString(user);
        String jsonExpected2 = mapper.writeValueAsString(user2);

        MvcResult result2 = mockMvc.perform(
                        get("/users/2")
                )
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(jsonExpected, result.getResponse().getContentAsString());
        assertEquals(jsonExpected2, result2.getResponse().getContentAsString());
    }

    @Test
    public void getAbsentUserByIdTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/users/2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUsersTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        UserDto user2 = UserDto.builder()
                .name("User2")
                .email("mail2@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        String json2 = mapper.writeValueAsString(user2);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/users")
                                .content(json2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("User"))
                .andExpect(jsonPath("$.[1].name").value("User2"));
    }

    @Test
    public void updateUserTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        UserDto newUser = UserDto.builder()
                .name("New user")
                .email("mail2@mail.ru")
                .build();
        json = mapper.writeValueAsString(newUser);
        mockMvc.perform(
                        patch("/users/1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andReturn();

        newUser.setId(1L);
        String jsonExpected = mapper.writeValueAsString(newUser);
        assertEquals(jsonExpected, result.getResponse().getContentAsString());
    }

    @Test
    public void updateAbsentUserTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        UserDto newUser = UserDto.builder()
                .name("New user")
                .email("mail2@mail.ru")
                .build();
        json = mapper.writeValueAsString(newUser);
        mockMvc.perform(
                        patch("/users/2")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/users/1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        patch("/users/1")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteNotFoundUserTest() throws Exception {
        UserDto user = UserDto.builder()
                .name("User")
                .email("mail@mail.ru")
                .build();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/users/2")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}