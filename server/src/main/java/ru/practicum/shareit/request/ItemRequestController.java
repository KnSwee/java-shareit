package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto dto) {
        log.debug("Запрос на создание ИтемРеквеста пользователем {}: {}", userId, dto);

        return service.createItemRequest(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение ИтемРеквеста по UserId {}", userId);
        return service.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение всех ИтемРеквест пользовтелем UserId {}", userId);
        return service.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId) {
        log.debug("Получение ИтемРеквеста по id {}", requestId);
        return service.getItemRequestById(requestId);
    }

}
