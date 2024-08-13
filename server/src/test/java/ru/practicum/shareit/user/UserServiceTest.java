package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    private User user = new User(1L, "user1", "first@user.ru");


    @Test
    void shouldUpdateUser() {
        UserDto returnUserDto = userService.create(user);
        user.setId(returnUserDto.getId());

        user.setName("newName");
        user.setEmail("new@email.ru");

        userService.update(user, returnUserDto.getId());
        UserDto updateUserDto = userService.get(returnUserDto.getId());

        assertThat(updateUserDto.getName(), equalTo("newName"));
        assertThat(updateUserDto.getEmail(), equalTo("new@email.ru"));
    }

    @Test
    void shouldExceptionWhenCreateUserWithExistingEmail() {
        user = new User(66L, "user2", "first@user.ru");
        userService.create(user);

        User newUser = new User(77L, "user3", "Re@user.ru");
        newUser.setEmail("first@user.ru");
        final EmailDuplicateException exception = assertThrows(EmailDuplicateException.class,
                () -> userService.create(newUser));
        assertEquals(String.format("Указанная почта first@user.ru уже привязана к аккаунту!"),
                exception.getMessage());
    }

    @Test
    void shouldExceptionWhenUpdateUserWithExistingEmail() {
        user = new User(2L, "user2", "second@user.ru");
        userService.create(user);

        User newUser = new User(3L, "user3", "third@user.ru");
        UserDto returnUserDto = userService.create(newUser);

        newUser.setId(returnUserDto.getId());
        newUser.setEmail("second@user.ru");

        final EmailDuplicateException exception = assertThrows(EmailDuplicateException.class,
                () -> userService.update(newUser, newUser.getId()));
        assertEquals(String.format("Указанная почта second@user.ru уже привязана к аккаунту!"),
                exception.getMessage());
    }

    @Test
    void shouldReturnUserWhenGetUserById() {
        UserDto returnUserDto = userService.create(user);

        assertThat(returnUserDto.getName(), equalTo(user.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldExceptionWhenGetUserWithNotExistingId() {
        user = new User(1L, "user2", "1@user.ru");
        userService.create(user);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.get(9999L));
        assertEquals(String.format("Указанный пользователь 9999 еще не создан!"),
                exception.getMessage());
    }

    @Test
    void shouldDeleteUser() {
        user = new User(10L, "user10", "ten@user.ru");
        UserDto returnUserDto = userService.create(user);

        userService.delete(returnUserDto.getId());
        List<UserDto> listUser = userService.getAll();

        assertThat(listUser.size(), equalTo(0));
    }


}