package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemWithCommentsDto extends ItemDto {

    private List<CommentDto> comments;

}
