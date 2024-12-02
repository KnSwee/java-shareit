package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemCreateDto itemCreateDto);

    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    ItemWithBookingDateAndCommentsDto getItemById(Long userId, Long itemId);

    List<ItemWithBookingDateAndCommentsDto> findAllUserItems(Long userId);

    List<ItemDto> getItemsToBook(String text);

    CommentDto createComment(Long userId, Long itemId, CommentCreationDto commentCreationDto);

    List<CommentDto> getCommentsByItemId(Long itemId);

    List<CommentDto> getCommentsByOwnerId(Long ownerId);

}
