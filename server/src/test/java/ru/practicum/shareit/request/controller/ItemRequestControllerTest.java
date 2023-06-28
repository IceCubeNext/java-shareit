package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionApiHandler;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private RequestDto requestDto;
    private RequestDto requestDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionApiHandler.class)
                .build();

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
    public void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(any(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(
                        get("/requests/1")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(requestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.items", is(requestDto.getItems())));
    }

    @Test
    public void getRequestByIdWithoutUserId() throws Exception {
        mvc.perform(
                        get("/requests/1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRequestsWithoutPageParameters() throws Exception {
        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto, requestDto2));

        mvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestorId", is(requestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].items", is(requestDto.getItems())))
                .andExpect(jsonPath("$.[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$.[1].requestorId", is(requestDto2.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[1].items", is(requestDto2.getItems())));
    }

    @Test
    public void getRequestsWithPageParameters() throws Exception {
        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto, requestDto2));

        mvc.perform(
                        get("/requests/all?from=0&size=10")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestorId", is(requestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].items", is(requestDto.getItems())))
                .andExpect(jsonPath("$.[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(requestDto2.getDescription())))
                .andExpect(jsonPath("$.[1].requestorId", is(requestDto2.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[1].items", is(requestDto2.getItems())));
    }

    @Test
    public void getRequestsWithoutUserId() throws Exception {
        mvc.perform(
                        get("/requests/all?from=0&size=10")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(requestDto));

        mvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestorId", is(requestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].items", is(requestDto.getItems())));
    }

    @Test
    public void getUserRequestsWithoutUserId() throws Exception {
        mvc.perform(
                        get("/requests")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addRequest() throws Exception {
        when(itemRequestService.addRequest(any(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(requestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(requestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.items", is(requestDto.getItems())));
    }

    @Test
    public void addRequestWithoutUserId() throws Exception {
        mvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(requestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}