package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto create(UserDto userDto);

    User getUser(Long id);

    UserDto getUserDto(Long id);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);
}
