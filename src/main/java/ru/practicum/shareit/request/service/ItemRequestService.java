package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {
    RequestDto getRequestById(Long id, Long userId);
    List<RequestDto> getUserRequests(Long userId);
    List<RequestDto> getRequests(Long userId, Integer from, Integer size);
    RequestDto addRequest(RequestDto requestDto, Long userId);
}
