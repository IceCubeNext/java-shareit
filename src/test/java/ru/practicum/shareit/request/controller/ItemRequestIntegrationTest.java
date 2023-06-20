package ru.practicum.shareit.request.controller;


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
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

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
public class ItemRequestIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private UserDto userDto;
    private UserDto userDto2;
    private RequestDto requestDto;

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

        requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужна дрель");
        requestDto.setRequestorId(1L);
        requestDto.setItems(Collections.emptyList());
    }

    @Test
    public void getRequestById() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());

        getRequestById(requestDto.getId(), userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    public void getRequestByIdUserNotFound() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());

        getRequestById(requestDto.getId(), 99L)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRequestByIdRequestNotFound() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());

        getRequestById(99L, userDto.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getEmptyRequests() throws Exception {
        postUser(userDto)
                .andExpect(status().isOk());
        getRequests(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUserRequests() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());
        getRequests(userDto.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class));
    }

    @Test
    public void getUserRequestsUserNotFound() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());
        getRequests(99L)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOtherRequests() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postUser(userDto2).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());
        getNotUserRequests(userDto2.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class));
    }

    @Test
    public void getOtherRequestsUserNotFound() throws Exception {
        postUser(userDto).andExpect(status().isOk());
        postRequest(userDto.getId(), requestDto).andExpect(status().isOk());
        getNotUserRequests(99L)
                .andExpect(status().isNotFound());
    }

    private ResultActions getRequestById(Long requestId, Long userId) throws Exception {
        return mockMvc.perform(
                get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
        );
    }

    private ResultActions getRequests(Long userId) throws Exception {
        return mockMvc.perform(
                get("/requests")
                        .header("X-Sharer-User-Id", userId)
        );
    }

    private ResultActions getNotUserRequests(Long userId) throws Exception {
        return mockMvc.perform(
                get("/requests/all")
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

    private ResultActions postRequest(Long userId, RequestDto requestDto) throws Exception {
        return mockMvc.perform(
                post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
        );
    }
}
