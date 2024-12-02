package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoMapperTest {

    @Test
    public void testUserDtoMapper() {
        User user = new User(1L, "user", "user@gmail.com");

        UserDto userDto = UserDtoMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        User mappedUser = UserDtoMapper.toUser(userDto);

        assertEquals(mappedUser.getId(), user.getId());
        assertEquals(mappedUser.getName(), userDto.getName());
        assertEquals(mappedUser.getEmail(), userDto.getEmail());
    }


}
