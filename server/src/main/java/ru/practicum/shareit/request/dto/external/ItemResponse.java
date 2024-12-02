package ru.practicum.shareit.request.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {

    Long itemId;

    String name;

    Long ownerId;

}
