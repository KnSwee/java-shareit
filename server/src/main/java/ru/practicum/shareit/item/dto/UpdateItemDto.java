package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}