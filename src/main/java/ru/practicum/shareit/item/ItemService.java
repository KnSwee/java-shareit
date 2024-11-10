package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, UpdateItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> findAllUserItems(long userId);

    List<ItemDto> getItemsToBook(String text);
}
