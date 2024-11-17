package ru.practicum.shareit.user;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistingDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;


    @Override
    public UserDto create(UserDto userDto) {
        if (repository.emailExists(userDto.getEmail())) {
            throw new ExistingDataException("Пользователь с таким email уже существует.");
        }
        if (StringUtils.isBlank(userDto.getName())) {
            throw new ValidationException("Нельзя создать пользователя без имени.");
        }
        if (StringUtils.isBlank(userDto.getEmail())) {
            throw new ValidationException("Нельзя создать пользователя без email");
        }
        return UserDtoMapper.toUserDto(repository.save(UserDtoMapper.toUser(userDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto get(Long id) {
        return UserDtoMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new ExistingDataException("Пользователя с таким id не существует.")));
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ExistingDataException("Пользователя с таким id не существует."));
        if (StringUtils.isNotBlank(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (StringUtils.isNotBlank(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        return UserDtoMapper.toUserDto(repository.save(user));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
