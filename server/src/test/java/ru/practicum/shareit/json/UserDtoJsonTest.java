package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJson;

    @Test
    public void userDtoTest() throws Exception {

        UserDto userDto = new UserDto(1L, "username", "user@gmail.com");

        JsonContent<UserDto> result = userDtoJson.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("username");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@gmail.com");
    }
}
