package ru.practicum.shareit.item.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public Item mapToItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setOwner(user);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId()
        );
    }

    public List<ItemDto> mapToItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }
}
