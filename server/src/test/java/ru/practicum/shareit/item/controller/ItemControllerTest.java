package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionApiHandler.class)
                .build();

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Дрель 'DeWalt' в отличном состоянии");
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);

        itemInfoDto = new ItemInfoDto();
        itemInfoDto.setId(2L);
        itemInfoDto.setName("Отвертка");
        itemInfoDto.setDescription("Отвертка крестовая новая");
        itemInfoDto.setAvailable(true);
        itemInfoDto.setOwner(1L);
        itemInfoDto.setComments(Collections.emptyList());
    }

    @Test
    public void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemInfoDto);

        mvc.perform(
                        get("/items/1")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfoDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemInfoDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.comments", is(itemInfoDto.getComments())));
    }

    @Test
    public void getItemByIdWithoutUserId() throws Exception {
        mvc.perform(
                        get("/items/1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getItemsWithoutPageParameters() throws Exception {
        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemInfoDto));

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemInfoDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner", is(itemInfoDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.[0].comments", is(itemInfoDto.getComments())));
    }

    @Test
    public void getItemsWithPageParameters() throws Exception {
        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemInfoDto));

        mvc.perform(
                        get("/items?from=0&size=10")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemInfoDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner", is(itemInfoDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.[0].comments", is(itemInfoDto.getComments())));
    }

    @Test
    public void getItemsWithoutUserId() throws Exception {
        mvc.perform(
                        get("/items")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItemsWithoutPageParameters() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(
                        get("/items/search?text=дрель")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    public void searchItemsWithPageParameters() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(
                        get("/items/search?text=дрель&from=0&size=10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    public void searchItemsWithoutText() throws Exception {
        mvc.perform(
                        get("/items/search")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void addItem() throws Exception {
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(itemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    public void addItemWithoutUserId() throws Exception {
        mvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Хорошая дрель");
        commentDto.setAuthorName("User");

        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(
                        post("/items/1/comment")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(commentDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    public void addCommentWithoutUserId() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Хорошая дрель");
        commentDto.setAuthorName("User");

        mvc.perform(
                        post("/items/1/comment")

                                .content(mapper.writeValueAsString(commentDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(itemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    public void updateItemWithoutUserId() throws Exception {
        mvc.perform(
                        patch("/items/1")
                                .content(mapper.writeValueAsString(itemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteItem() throws Exception {
        mvc.perform(
                        delete("/items/1")
                )
                .andExpect(status().isOk());
    }
}