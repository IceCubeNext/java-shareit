package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;
import java.util.Date;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private Date created;
}
