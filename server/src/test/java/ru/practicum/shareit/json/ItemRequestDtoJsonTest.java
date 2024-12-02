package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestJson;

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJson;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        UserDto userDto = new UserDto(1L, "userDto", "user@gmail.com");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", 1L, created, null);

        JsonContent<ItemRequestDto> result = itemRequestJson.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

}
