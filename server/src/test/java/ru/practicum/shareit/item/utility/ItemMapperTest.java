package ru.practicum.shareit.item.utility;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    @Test
    public void mapToItemTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Дрель новая");
        itemDto.setAvailable(true);
        itemDto.setOwner(user.getId());

        Item item = ItemMapper.mapToItem(itemDto, user);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(user, item.getOwner());
    }

    @Test
    public void mapToItemDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getOwner().getId(), itemDto.getOwner());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    public void mapToItemDtoRequestTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User2");
        user2.setEmail("mail2@mail.ru");

        Request request = new Request();
        request.setId(1L);
        request.setDescription("нужна дрель");
        request.setRequestor(user2);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);
        item.setRequest(request);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getOwner().getId(), itemDto.getOwner());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    public void mapToItemInfoDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        ItemInfoDto itemDto = ItemMapper.mapToItemInfoDto(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getOwner().getId(), itemDto.getOwner());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getComments());
    }

    @Test
    public void mapToCommentTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("комментарий");

        Comment comment = ItemMapper.mapToComment(commentCreateDto, user, item);
        assertEquals(commentCreateDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
        assertNotNull(comment.getCreated());
    }

    @Test
    public void mapToCommentDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Дрель");
        item.setDescription("Дрель в отличном состоянии");
        item.setAvailable(true);

        LocalDateTime time = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("комментарий");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(time);

        CommentDto commentDto = ItemMapper.mapToCommentDto(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}