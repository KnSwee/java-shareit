package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDtoMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(item.getRequest());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setDescription(itemDto.getDescription());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequest());
        return item;
    }

    public static Item updateToItem(UpdateItemDto updateItemDto) {
        Item item = new Item();
        item.setName(updateItemDto.getName());
        item.setDescription(updateItemDto.getDescription());
        item.setAvailable(updateItemDto.getAvailable());
        item.setRequest(updateItemDto.getRequest());
        return item;
    }

}
