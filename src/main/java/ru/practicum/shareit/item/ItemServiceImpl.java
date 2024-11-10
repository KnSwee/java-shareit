package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemRepository repository;
    private final InMemoryUserRepository userRepository;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (userRepository.get(userId) == null) {
            throw new NotFoundException("Пользователя с переданным айди не существует");
        }
        Item item = ItemDtoMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemDtoMapper.toItemDto(repository.create(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, UpdateItemDto itemDto) {
        if (repository.get(itemId).getOwnerId() != userId) {
            throw new ForbiddenException("У вас недостаточно прав для редактирования этой вещи.");
        }
        Item item = ItemDtoMapper.updateToItem(itemDto);
        item.setId(itemId);
        return ItemDtoMapper.toItemDto(repository.update(item));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemDtoMapper.toItemDto(repository.get(itemId));
    }

    @Override
    public List<ItemDto> findAllUserItems(long userId) {
        return repository.findAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(ItemDtoMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItemsToBook(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return repository.findAll().stream()
                .filter(item -> {
                    if (item.getName() == null || item.getDescription() == null) {
                        return false;
                    }
                    return item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()));
                })
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
