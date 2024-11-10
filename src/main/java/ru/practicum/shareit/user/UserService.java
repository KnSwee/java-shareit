package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto get(Long id);

    UserDto update(long id, UserDto userDto);

    void delete(Long id);
}
