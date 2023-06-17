package ru.practicum.shareit.request.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public Request mapToRequest(RequestDto requestDto, User requestor) {
        Request request = new Request();
        request.setDescription(requestDto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);
        return request;
    }

    public RequestDto mapToRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated(),
                Collections.emptyList()
        );
    }

    public List<RequestDto> mapToRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::mapToRequestDto).collect(Collectors.toList());
    }
}
