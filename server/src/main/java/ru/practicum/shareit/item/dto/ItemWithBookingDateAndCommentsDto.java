package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingDateAndCommentsDto extends ItemDto {


    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;

    public ItemWithBookingDateAndCommentsDto(Long id, String name, String description, Boolean available, ItemRequestDto request, LocalDateTime lastBooking, LocalDateTime nextBooking, List<CommentDto> comments) {
        super(id, name, description, available, request);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
