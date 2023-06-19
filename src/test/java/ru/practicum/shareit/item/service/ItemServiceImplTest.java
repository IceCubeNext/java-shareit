package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("User");
        owner.setEmail("mail@mail.ru");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.ru");

        item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Новая дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setText("Отличная вещь");
    }

    @Test
    public void getItemByIdOwner() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findLastBooking(item.getId()))
                .thenReturn(booking);

        when(bookingRepository.findNextBooking(item.getId()))
                .thenReturn(booking);

        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));

        ItemInfoDto actual = itemService.getItemById(item.getId(), owner.getId());

        assertEquals(item.getId(), actual.getId());
        assertEquals(item.getOwner().getId(), actual.getOwner());
        assertEquals(booking.getId(), actual.getLastBooking().getId());
        assertEquals(booking.getId(), actual.getNextBooking().getId());
        assertEquals(1, actual.getComments().size());
        assertEquals(1L, actual.getComments().get(0).getId());
    }

    @Test
    public void getItemByIdNotOwner() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        ItemInfoDto actual = itemService.getItemById(item.getId(), booker.getId());

        assertEquals(item.getId(), actual.getId());
        assertEquals(item.getOwner().getId(), actual.getOwner());
        assertNull(actual.getLastBooking());
        assertNull(actual.getNextBooking());
        assertEquals(Collections.emptyList(), actual.getComments());
    }

    @Test
    public void getItemByIdNotFoundItem() {
        when(itemRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L, booker.getId()));
    }

    @Test
    public void getItemByIdLastNotNull() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findLastBooking(item.getId()))
                .thenReturn(booking);

        ItemInfoDto actual = itemService.getItemById(item.getId(), owner.getId());

        assertEquals(booking.getId(), actual.getLastBooking().getId());
        assertNull(actual.getNextBooking());
    }

    @Test
    public void getItemByIdNextNotNull() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findNextBooking(item.getId()))
                .thenReturn(booking);

        ItemInfoDto actual = itemService.getItemById(item.getId(), owner.getId());

        assertEquals(booking.getId(), actual.getNextBooking().getId());
        assertNull(actual.getLastBooking());
    }

    @Test
    public void getItemByIdNotOwnerCheckLastNext() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        ItemInfoDto actual = itemService.getItemById(item.getId(), booker.getId());

        assertNull(actual.getLastBooking());
        assertNull(actual.getNextBooking());
    }

    @Test
    public void getItemsByUserIdNotFoundUser() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getItemsByUserId(99L, 0, 10));
    }

    @Test
    public void searchItemsBlankText() {
        List<ItemDto> actual = itemService.searchItems("", 0, 10);
        assertEquals(0, actual.size());
    }

    @Test
    public void searchItemsTextFromManySpace() {
        List<ItemDto> actual = itemService.searchItems("       ", 0, 10);
        assertEquals(0, actual.size());
    }

    @Test
    public void searchItems() {
        final Page<Item> page = new PageImpl<>(List.of(item));
        when(itemRepository.findText(any(), any()))
                .thenReturn(page);

        List<ItemDto> actual = itemService.searchItems("text", 0, 10);

        assertEquals(1, page.getContent().size(), actual.size());
        assertEquals(page.getContent().get(0).getId(), actual.get(0).getId());
        assertEquals(page.getContent().get(0).getName(), actual.get(0).getName());
    }

    @Test
    public void addItemWithoutRequest() {
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto actual = itemService.addItem(owner.getId(), ItemMapper.mapToItemDto(item));

        assertEquals(item.getId(), actual.getId());
        assertEquals(item.getName(), actual.getName());
    }

    @Test
    public void addItemWithRequest() {
        Request request = new Request();
        request.setId(11L);
        when(userService.getUser(owner.getId()))
                .thenReturn(owner);

        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(request));

        when(itemRepository.save(any()))
                .thenReturn(item);

        item.setRequest(request);
        ItemDto actual = itemService.addItem(owner.getId(), ItemMapper.mapToItemDto(item));

        assertEquals(item.getId(), actual.getId());
        assertEquals(item.getName(), actual.getName());
        assertEquals(item.getRequest().getId(), actual.getRequestId());
    }

    @Test
    public void addItemUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addItem(99L, ItemMapper.mapToItemDto(item)));
    }

    @Test
    public void addCommentUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(99L, item.getId(), new CommentCreateDto("text")));
    }

    @Test
    public void addCommentItemNotFound() {
        when(itemRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(owner.getId(), 99L, new CommentCreateDto("text")));
    }

    @Test
    public void updateItemItemNotFound() {
        when(itemRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(owner.getId(), 99L, ItemMapper.mapToItemDto(item)));
    }

    @Test
    public void updateItemNotOwner() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(booker.getId(), item.getId(), ItemMapper.mapToItemDto(item)));
    }

}

