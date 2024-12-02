package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.HandleValidator;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated @RequestBody ItemCreationRequestDto itemCreateDto,
                                         BindingResult bindingResult) {
        log.info("Creating new item: {}", itemCreateDto);
        HandleValidator.handle(bindingResult, log);
        return itemClient.addItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @RequestBody ItemUpdateRequestDto itemDto) {
        log.info("Updating existing item {} by user {}: {}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Getting item {} by user {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Finding all items by user {}", userId);
        return itemClient.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsToBook(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam("text") String text) {
        if (text.isBlank()) {
            return ResponseEntity.of(Optional.of(new ArrayList<>()));
        }
        log.info("Getting items to book for user {} with text {}", userId, text);
        return itemClient.getItemsToBook(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable("itemId") Long itemId,
                                                @RequestBody CommentCreationDto commentCreationDto) {
        log.info("Creating comment for item {} with text {}", itemId, commentCreationDto);
        return itemClient.createComment(userId, itemId, commentCreationDto);
    }

}
