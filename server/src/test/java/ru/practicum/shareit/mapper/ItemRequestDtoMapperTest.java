package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestDtoMapperTest {

    @Test
    public void testItemRequestDtoMapper() {

        ItemRequest itemRequest = new ItemRequest(1L, "descr", LocalDateTime.now().minusDays(1), new User(1L, "requester", "mail@mail.com"));

        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getRequesterId(), itemRequest.getRequester().getId());
        assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());

        ItemRequestDtoMapper.toItemRequest(itemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getRequesterId(), itemRequest.getRequester().getId());
        assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
    }

}
