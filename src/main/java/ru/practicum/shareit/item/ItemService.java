package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, UpdateItemDto itemDto);

    ItemWithBookingDateAndCommentsDto getItemById(Long userId, Long itemId);

    List<ItemWithBookingDateAndCommentsDto> findAllUserItems(long userId);

    List<ItemDto> getItemsToBook(String text);

    CommentDto createComment(long userId, Long itemId, CommentCreationDto commentCreationDto);

    List<CommentDto> getCommentsByItemId(long itemId);

    List<CommentDto> getCommentsByOwnerId(long ownerId);

}
