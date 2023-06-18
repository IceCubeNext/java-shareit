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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private RequestDto requestDto2;


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

        requestDto2 = new RequestDto();
        requestDto2.setId(2L);
        requestDto2.setDescription("Нужна отвертка");
        requestDto2.setRequestorId(2L);
        requestDto2.setItems(Collections.emptyList());
    }

    @Test
    public void getEmptyRequests() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUserRequestsUser1() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(mapper.writeValueAsString(userDto2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/requests")
                                .content(mapper.writeValueAsString(requestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/requests")
                                .content(mapper.writeValueAsString(requestDto2))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestorId", is(requestDto.getRequestorId()), Long.class));
    }

    @Test
    public void getOtherRequestsUser1() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(mapper.writeValueAsString(userDto2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/requests")
                                .content(mapper.writeValueAsString(requestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/requests")
                                .content(mapper.writeValueAsString(requestDto2))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$.[0].requestorId", is(requestDto2.getRequestorId()), Long.class));
    }
}
