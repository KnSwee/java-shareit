package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.ExistingDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final UserService userService;
    private final UserRepository userRepository;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userDto = new UserDto();
        userDto.setName("TestUserDto");
        userDto.setEmail("testDto@test.com");

        user = new User();
        user.setName("TestUser");
        user.setEmail("test@test.com");
        userRepository.save(user);
    }

    @Test
    void userCreateTest() {
        UserDto userDto1 = userService.create(userDto);

        assertThat(userDto1, notNullValue());
        assertThat(userDto1.getEmail(), equalTo("testDto@test.com"));
        assertThat(userDto1.getName(), equalTo("TestUserDto"));
    }

    @Test
    void userUpdateTest() {
        UserDto updatedUSer = userService.update(user.getId(), userDto);

        assertThat(updatedUSer.getName(), equalTo("TestUserDto"));
        assertThat(updatedUSer.getEmail(), equalTo("testDto@test.com"));
    }

    @Test
    void getUserTest() {
        UserDto gettedUser = userService.getUserDto(user.getId());

        assertThat(gettedUser.getName(), equalTo("TestUser"));
        assertThat(gettedUser.getEmail(), equalTo("test@test.com"));
        assertThat(gettedUser.getId(), equalTo(user.getId()));
    }

    @Test
    void deleteUserTest() {
        userService.delete(user.getId());

        assertThat(userRepository.findById(user.getId()), is(Optional.empty()));
    }

    @Test
    void userCreateExceptionTest() {
        try {
            userService.create(new UserDto(10L, "user", "test@test.com"));
        } catch (ExistingDataException e) {
            assertThat(e.getMessage(), containsString("Пользователь с таким email уже существует."));
        }
    }

    @Test
    void userCreateNameExceptionTest() {
        try {
            userService.create(new UserDto(10L, "", "newtest@test.com"));
        } catch (ValidationException e) {
            assertThat(e.getMessage(), containsString("Нельзя создать пользователя без имени."));
        }
    }

    @Test
    void userCreateEmailExceptionTest() {
        try {
            userService.create(new UserDto(10L, "user", ""));
        } catch (ValidationException e) {
            assertThat(e.getMessage(), containsString("Нельзя создать пользователя без email"));
        }
    }

}
