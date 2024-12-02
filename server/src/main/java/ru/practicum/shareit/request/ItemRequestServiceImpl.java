package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.external.ItemResponse;
import ru.practicum.shareit.request.model.ItemPOJO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(userService.getUser(userId));
        return ItemRequestDtoMapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {
        return repository.getRequestedItems(userId).stream()
                .collect(Collectors.groupingBy(ItemPOJO::getItemRequest,
                        Collectors.mapping(ItemPOJO::getItem, Collectors.toSet())))
                .entrySet()
                .stream()
                .map(itemRequestListEntry -> {
                            ItemRequestDto itemRequestDto = ItemRequestDtoMapper.toItemRequestDto(itemRequestListEntry.getKey());
                            itemRequestDto.setItems(itemRequestListEntry.getValue()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(item -> {
                                        ItemResponse itemResponse = new ItemResponse();
                                        itemResponse.setName(item.getName());
                                        itemResponse.setItemId(item.getId());
                                        itemResponse.setOwnerId(item.getOwner().getId());
                                        return itemResponse;
                                    })
                                    .toList());
                            return itemRequestDto;
                        }
                )
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Long userId) {
        return repository.findAll().stream()
                .filter(itemRequest -> !Objects.equals(itemRequest.getRequester().getId(), userId))
                .map(ItemRequestDtoMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        return repository.getRequestedItemById(requestId).stream()
                .collect(Collectors.groupingBy(ItemPOJO::getItemRequest,
                        Collectors.mapping(ItemPOJO::getItem, Collectors.toList())))
                .entrySet().stream()
                .map(itemRequestListEntry -> {
                            ItemRequestDto itemRequestDto = ItemRequestDtoMapper.toItemRequestDto(itemRequestListEntry.getKey());
                            itemRequestDto.setItems(itemRequestListEntry.getValue()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(item -> {
                                        ItemResponse itemResponse = new ItemResponse();
                                        itemResponse.setName(item.getName());
                                        itemResponse.setItemId(item.getId());
                                        itemResponse.setOwnerId(item.getOwner().getId());
                                        return itemResponse;
                                    })
                                    .toList());
                            return itemRequestDto;
                        }
                )
                .toList().getFirst();
    }
}
