package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDtoMapperTest {

    @Test
    public void testItemDtoMapper() {
        Item item = new Item(1L, "item", "description", true, new User(1L, "user",
                "user@gmail.com"), null);

        ItemDto itemDto = ItemDtoMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());

        Item mapedItem = ItemDtoMapper.toItem(itemDto);

        assertEquals(mapedItem.getId(), item.getId());
        assertEquals(mapedItem.getName(), itemDto.getName());
        assertEquals(mapedItem.getDescription(), itemDto.getDescription());
        assertEquals(mapedItem.getAvailable(), itemDto.getAvailable());

        Item createItem = ItemDtoMapper.toItem(new ItemCreateDto(2L, "createItem", "create", true, null));
        assertEquals(createItem.getId(), 2);
        assertEquals(createItem.getName(), "createItem");
        assertEquals(createItem.getDescription(), "create");
        assertEquals(createItem.getAvailable(), true);

    }

}
