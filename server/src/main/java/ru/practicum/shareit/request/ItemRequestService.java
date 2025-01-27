package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequestsByUserId(Long userId);

    List<ItemRequestDto> getItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long requestId);

}
