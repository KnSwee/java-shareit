package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDtoMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(ItemRequestDtoMapper.toItemRequestDto(item.getRequest()));
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setDescription(itemDto.getDescription());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(ItemRequestDtoMapper.toItemRequest(itemDto.getRequest()));
        return item;
    }

    public static Item toItem(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setId(itemCreateDto.getId());
        item.setDescription(itemCreateDto.getDescription());
        item.setName(itemCreateDto.getName());
        item.setAvailable(itemCreateDto.getAvailable());
        return item;
    }

    public static List<ItemDto> toItemDto(Iterable<Item> items) {
        List<ItemDto> itemDtoMap = new ArrayList<>();
        for (Item item : items) {
            itemDtoMap.add(ItemDtoMapper.toItemDto(item));
        }
        return itemDtoMap;
    }

}
