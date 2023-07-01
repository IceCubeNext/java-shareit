package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.utility.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private User requestor;
    private Request request;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("User");
        requestor.setEmail("mail@mail.ru");

        request = new Request();
        request.setId(1L);
        request.setDescription("Ищу дрель");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
    }

    @Test
    public void getRequestById() {
        when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));

        when(userService.getUser(requestor.getId()))
                .thenReturn(requestor);

        when(itemRepository.findAllByRequestOrderByRequestCreatedDesc(request))
                .thenReturn(Collections.emptyList());

        RequestDto actual = itemRequestService.getRequestById(request.getId(), requestor.getId());

        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getRequestor().getId(), actual.getRequestorId());
    }

    @Test
    public void getRequestByIdUserNotFound() {
        when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));

        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(request.getId(), 99L));
    }

    @Test
    public void getRequestByIdRequestNotFound() {
        when(itemRequestRepository.findById(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(99L, requestor.getId()));
    }

    @Test
    public void getUserRequestsUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(99L));
    }

    @Test
    public void getRequestsUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequests(99L, 0, 10));
    }

    @Test
    public void addRequestsUserNotFound() {
        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(RequestMapper.mapToRequestDto(request), 99L));
    }
}