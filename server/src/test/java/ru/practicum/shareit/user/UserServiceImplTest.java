package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

    @SpringBootTest
    @ExtendWith(MockitoExtension.class)
    public class UserServiceImplTest {

        @Autowired
        private UserMapper userMapper;

        @Mock
        private UserRepository mockUserRepository;

        @Test
        void shouldExceptionWhenCreateUserWithExistingEmail() {
            UserService userService = new UserServiceImpl(mockUserRepository, userMapper);

            User user = new User(1L, "user1", "first@user.ru");
            userService.create(user);

            when(mockUserRepository.save(any(User.class)))
                    .thenThrow(DataIntegrityViolationException.class);

            User newUser = new User(2L, "newName", user.getEmail());

            final EmailDuplicateException exception = assertThrows(EmailDuplicateException.class,
                    () -> userService.create(newUser));
            assertEquals(String.format("Указанная почта %s уже привязана к аккаунту!", newUser.getEmail()),
                    exception.getMessage());
        }
    }