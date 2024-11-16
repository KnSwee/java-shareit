package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("itemId") Long itemId,
                          @RequestBody UpdateItemDto itemDto) {
        return service.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDateAndCommentsDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable("itemId") Long itemId) {
        return service.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingDateAndCommentsDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsToBook(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam("text") String text) {
        return service.getItemsToBook(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable("itemId") Long itemId,
                                    @RequestBody CommentCreationDto commentCreationDto) {
        return service.createComment(userId, itemId, commentCreationDto);
    }

}
