package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentDtoMapper {

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setItem(ItemDtoMapper.toItemDto(comment.getItem()));
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreatedAt());
        return dto;

    }

    public static List<CommentDto> toDto(Iterable<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toDto(comment));
        }
        return dtos;
    }

}
