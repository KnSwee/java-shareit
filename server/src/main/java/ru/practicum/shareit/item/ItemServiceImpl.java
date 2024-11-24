package ru.practicum.shareit.item;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.LastOrNextBookingDto;
import ru.practicum.shareit.exception.ExistingDataException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private static void itemMapping(ItemWithBookingDateAndCommentsDto itemDto, Item item) {
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(ItemRequestDtoMapper.toItemRequestDto(item.getRequest()));
    }

    @Override
    public ItemDto create(Long userId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с переданным айди не существует"));
        if (StringUtils.isBlank(itemCreateDto.getName())) {
            throw new ValidationException("Нельзя создать вещь без названия.");
        }
        Item item = ItemDtoMapper.toItem(itemCreateDto);
        item.setOwner(owner);
        if (itemCreateDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запроса на вещь с переданным айди не существует"));
            item.setRequest(request);
        }
        return ItemDtoMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с переданным айди не существует"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с переданным айди не существует"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ForbiddenException("У вас недостаточно прав для редактирования этой вещи.");
        }
        if (StringUtils.isNotBlank(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequest() != null) {
            item.setRequest(itemDto.getRequest());
        }

        return ItemDtoMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingDateAndCommentsDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с переданным айди не существует"));
        List<CommentDto> commentDtos = getCommentsByItemId(itemId);

        ItemWithBookingDateAndCommentsDto itemDto = new ItemWithBookingDateAndCommentsDto();
        itemMapping(itemDto, item);
        if (Objects.equals(userId, item.getOwner().getId())) {
            itemDto.setLastBooking(bookingRepository.findLastBookingDateByItemId(itemId));
            itemDto.setNextBooking(bookingRepository.findNextBookingDateByItemId(itemId));
        }
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingDateAndCommentsDto> findAllUserItems(Long ownerId) {
        Map<Long, Item> itemsDto = itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
        Map<Long, LocalDateTime> lastBookingsDates = bookingRepository.findLastBookingsDates(ownerId)
                .stream()
                .collect(Collectors.toMap(LastOrNextBookingDto::getItemId, LastOrNextBookingDto::getDateTime));
        Map<Long, LocalDateTime> nextBookingsDates = bookingRepository.findNextBookingsDates(ownerId)
                .stream()
                .collect(Collectors.toMap(LastOrNextBookingDto::getItemId, LastOrNextBookingDto::getDateTime));
        ;
        Map<Long, List<CommentDto>> comments = getCommentsByOwnerId(ownerId).stream()
                .collect(Collectors.groupingBy(commentDto -> commentDto.getItem().getId(), Collectors.toList()));

        return itemsDto.entrySet()
                .stream()
                .map(entry -> {
                    Long itemId = entry.getKey();
                    ItemWithBookingDateAndCommentsDto itemDto = new ItemWithBookingDateAndCommentsDto();
                    itemMapping(itemDto, entry.getValue());
                    itemDto.setLastBooking(lastBookingsDates.get(itemId));
                    itemDto.setNextBooking(nextBookingsDates.get(itemId));
                    itemDto.setComments(comments.get(itemId));
                    return itemDto;
                })
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsToBook(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemDtoMapper.toItemDto(itemRepository.getItemsToBook(text));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) {
        if (!bookingRepository.checkBookingExist(itemId, userId)) {
            throw new ValidationException("Комментарий может быть оставлен только арендатором вещи.");
        }
        Comment comment = new Comment();
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new ExistingDataException("")));
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new ExistingDataException("")));
        comment.setText(commentCreationDto.getText());
        return CommentDtoMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return CommentDtoMapper.toDto(commentRepository.findByItemId(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByOwnerId(Long ownerId) {
        return commentRepository.getCommentsByOwnerId(ownerId).stream()
                .filter(Objects::nonNull)
                .map(CommentDtoMapper::toDto)
                .toList();
    }

}
