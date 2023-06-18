package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.utility.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public RequestDto getRequestById(Long id, Long userId) {
        Request request = getRequest(id);
        getUser(userId);
        RequestDto requestDto = RequestMapper.mapToRequestDto(request);
        requestDto.setItems(itemRepository.findAllByRequestOrderByRequestCreatedDesc(request).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList()));
        return requestDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getUserRequests(Long userId) {
        User user = getUser(userId);
        List<Request> requests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(user);
        return setItemsInRequest(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long userId, Integer from, Integer size) {
        User user = getUser(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Request> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user, page).getContent();
        return setItemsInRequest(requests);
    }

    @Override
    @Transactional
    public RequestDto addRequest(RequestDto requestDto, Long userId) {
        User user = getUser(userId);
        Request request = RequestMapper.mapToRequest(requestDto, user);
        return RequestMapper.mapToRequestDto(itemRequestRepository.save(request));
    }

    private List<RequestDto> setItemsInRequest(List<Request> requests) {
        Map<Request, List<Item>> items = itemRepository.findByRequestIn(requests, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));

        List<RequestDto> requestsDto = new ArrayList<>();
        for (Request request : requests) {
            RequestDto requestDto = RequestMapper.mapToRequestDto(request);
            if (items.get(request) != null) {
                requestDto.setItems(items.get(request).stream().map(ItemMapper::mapToItemDto).collect(toList()));
            }
            requestsDto.add(requestDto);
        }
        return requestsDto;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", id)));
    }

    private Request getRequest(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", id)));
    }
}
