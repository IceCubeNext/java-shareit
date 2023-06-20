package ru.practicum.shareit.request.utility;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RequestMapperTest {
    @Test
    public void mapToRequestTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        LocalDateTime time = LocalDateTime.now();
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Нужна дрель");
        requestDto.setCreated(time);
        requestDto.setRequestorId(user.getId());

        Request request = RequestMapper.mapToRequest(requestDto, user);
        assertEquals(requestDto.getDescription(), request.getDescription());
        assertEquals(requestDto.getCreated(), request.getCreated());
        assertEquals(requestDto.getRequestorId(), request.getRequestor().getId());
    }

    @Test
    public void mapToRequestDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.ru");

        LocalDateTime time = LocalDateTime.now();
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setCreated(time);
        request.setRequestor(user);

        RequestDto requestDto = RequestMapper.mapToRequestDto(request);

        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getDescription(), requestDto.getDescription());
        assertEquals(request.getRequestor().getId(), requestDto.getRequestorId());
        assertEquals(request.getCreated(), requestDto.getCreated());
    }
}