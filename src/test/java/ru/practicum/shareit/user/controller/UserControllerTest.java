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
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");
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
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail.ru");
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyMail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("");
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("mail@mail.ru");
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithExistsMail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        userDto.setName("User2");
        userDto.setEmail("mail@mail.ru");
        json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setName("User2");
        userDto2.setEmail("mail2@mail.ru");
        String json = mapper.writeValueAsString(userDto);
        String json2 = mapper.writeValueAsString(userDto2);
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

        userDto.setId(1L);
        userDto2.setId(2L);
        String jsonExpected = mapper.writeValueAsString(userDto);
        String jsonExpected2 = mapper.writeValueAsString(userDto2);

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
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");
        String json = mapper.writeValueAsString(userDto);
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
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setName("User2");
        userDto2.setEmail("mail2@mail.ru");
        String json = mapper.writeValueAsString(userDto);
        String json2 = mapper.writeValueAsString(userDto2);
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
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");

        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        UserDto userDto2 = new UserDto();
        userDto2.setName("User2");
        userDto2.setEmail("mail2@mail.ru");

        json = mapper.writeValueAsString(userDto2);
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

        userDto2.setId(1L);
        String jsonExpected = mapper.writeValueAsString(userDto2);
        assertEquals(jsonExpected, result.getResponse().getContentAsString());
    }

    @Test
    public void updateAbsentUserTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        UserDto userDto2 = new UserDto();
        userDto.setName("User2");
        userDto.setEmail("mail2@mail.ru");
        json = mapper.writeValueAsString(userDto2);
        mockMvc.perform(
                        patch("/users/2")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");
        String json = mapper.writeValueAsString(userDto);
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
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("mail@mail.ru");
        String json = mapper.writeValueAsString(userDto);
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