package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDateAndCommentsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> commentJson;

    @Autowired
    private JacksonTester<ItemDto> itemJson;

    @Autowired
    private JacksonTester<ItemWithBookingDateAndCommentsDto> itemWithAddsJson;

    final ItemDto itemDto = new ItemDto(1L, "itemDto", "descriptionItemDto", true, null);

    @Test
    void testItemDto() throws Exception {
        JsonContent<ItemDto> result = itemJson.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
    }

    @Test
    void testCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        CommentDto commentDto = new CommentDto(1L, "text", itemDto, "author", created);

        JsonContent<CommentDto> result = commentJson.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
    }

    @Test
    void testItemWithBookingDateAndCommentsDto() throws Exception {
        LocalDateTime lastBooking = LocalDateTime.now().minusDays(3);
        LocalDateTime nextBooking = LocalDateTime.now().plusDays(5);
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        CommentDto commentDto = new CommentDto(1L, "good!", itemDto, "author", created);


        ItemWithBookingDateAndCommentsDto itemWithAdds = new ItemWithBookingDateAndCommentsDto(
                1L, "itemWithAdds", "descr", true,
                null, lastBooking, nextBooking, List.of(commentDto));

        JsonContent<ItemWithBookingDateAndCommentsDto> result = itemWithAddsJson.write(itemWithAdds);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemWithAdds.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemWithAdds.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemWithAdds.getAvailable());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(lastBooking.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(nextBooking.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(commentDto.getText());
    }
}
