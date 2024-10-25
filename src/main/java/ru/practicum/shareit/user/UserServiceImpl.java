package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistingDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        if (repository.emailCheck(userDto.getEmail())) {
            throw new ExistingDataException("Пользователь с таким email уже существует.");
        }
        return UserDtoMapper.toUserDto(repository.create(UserDtoMapper.toUser(userDto)));
    }

    @Override
    public UserDto get(Long id) {
        return UserDtoMapper.toUserDto(repository.get(id));
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        return UserDtoMapper.toUserDto(repository.update(id, UserDtoMapper.toUser(userDto)));
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
