package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private UserDto userDto;
    private UserDto userDto2;

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
    }

    @Test
    public void addNormalUser() throws Exception {
        postUser(userDto).andExpect(status().isOk());
    }

    @Test
    public void addUserWithIncorrectEmail() throws Exception {
        userDto.setEmail("mail.ru");
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyMail() throws Exception {
        userDto.setEmail("");
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithBankMail() throws Exception {
        userDto.setEmail("       ");
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithNullMail() throws Exception {
        userDto.setEmail(null);
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyName() throws Exception {
        userDto.setName("");
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithBankName() throws Exception {
        userDto.setName("       ");
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithNullName() throws Exception {
        userDto.setName(null);
        postUser(userDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithExistsMail() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto2.setEmail(userDto.getEmail());
        postUser(userDto2).andExpect(status().isInternalServerError());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        getUserById(userDto.getId()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
        getUserById(userDto2.getId()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto2.getId()), Long.class));
    }

    @Test
    public void getUserByIdUserNotFoundTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        getUserById(99L).andExpect(status().isNotFound());
    }

    @Test
    public void getEmptyUsers() throws Exception {
        getUsers()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUsersTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());

        getUsers()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("User"))
                .andExpect(jsonPath("$.[1].name").value("User2"));
    }

    @Test
    public void updateUserNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setName("New name");
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));
    }

    @Test
    public void updateUserNameEmptyNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setName("");
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"));
    }

    @Test
    public void updateUserNameBlankNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setName("          ");
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"));
    }

    @Test
    public void updateUserNameNullNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setName(null);
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"));
    }

    @Test
    public void updateUserEmailTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setEmail("yandex@praktikum.ru");
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("yandex@praktikum.ru"));
    }

    @Test
    public void updateUserEmailEmptyNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setEmail("");
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail@mail.ru"));
    }

    @Test
    public void updateUserEmailNullNameTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        userDto.setEmail(null);
        patchUser(userDto.getId(), userDto).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail@mail.ru"));
    }

    @Test
    public void updateAbsentUserTest() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        patchUser(99L, userDto).andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserTest() throws Exception {
        postUser(userDto).andExpect(status().isOk()).andExpect(status().isOk());
        getUserById(userDto.getId()).andExpect(status().isOk());
        deleteUser(userDto.getId()).andExpect(status().isOk());
        getUserById(userDto.getId()).andExpect(status().isNotFound());
    }

    @Test
    public void deleteNotFoundUserTest() throws Exception {
        postUser(userDto).andExpect(status().isOk()).andExpect(status().isOk());
        getUserById(userDto.getId()).andExpect(status().isOk());
        deleteUser(99L).andExpect(status().isNotFound());
    }

    private ResultActions getUserById(Long userId) throws Exception {
        return mockMvc.perform(
                get("/users/" + userId)
        );
    }

    private ResultActions getUsers() throws Exception {
        return mockMvc.perform(
                get("/users")
        );
    }

    private ResultActions postUser(UserDto userDto) throws Exception {
        return mockMvc.perform(
                post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions patchUser(Long userId, UserDto userDto) throws Exception {
        return mockMvc.perform(
                patch("/users/" + userId)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions deleteUser(Long userId) throws Exception {
        return mockMvc.perform(
                delete("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}