package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    private final RequestService RequestService;
    private final UserService userService;

    private final User user1 = new User(101L, "AlexOne", "alexone@alex.ru");
    private final UserDto userDto1 = new UserDto(101L, "AlexOne", "alexone@alex.ru");
    private final User user2 = new User(102L, "AlexTwo", "alextwo@alex.ru");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(100L, "ItemRequest description", userDto1,
            LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @Test
    void shouldCreateItemRequest() {
        UserDto newUserDto = userService.create(user1);
        ItemRequestDto returnRequestDto = RequestService.create(itemRequestDto, newUserDto.getId());

        assertThat(returnRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void shouldExceptionWhenCreateItemRequestWithWrongUserId() {
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> RequestService.create(itemRequestDto, -2L));

        assertEquals("Пользователь c id = -2 не найден!", exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetItemRequestWithWrongId() {
        UserDto userDto = userService.create(user1);

        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> RequestService.getById(-2L, userDto.getId()));
        assertEquals("Запрос c id = -2 не найден!", exp.getMessage());
    }

    @Test
    void shouldReturnAllItemRequestsWhenSizeNotNull() {
        UserDto userDto = userService.create(user1);
        UserDto requesterDto = userService.create(user2);

        RequestService.create(itemRequestDto, requesterDto.getId());
        RequestService.create(itemRequestDto, requesterDto.getId());
        List<ItemRequestDto> listItemRequest = RequestService.getAll(0, 10, userDto.getId());

        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnAllItemRequestsWhenSizeIsNull() {
        UserDto userDto = userService.create(user1);
        UserDto requesterDto = userService.create(user2);

        RequestService.create(itemRequestDto, requesterDto.getId());
        RequestService.create(itemRequestDto, requesterDto.getId());
        List<ItemRequestDto> listItemRequest = RequestService.getAll(0, null, userDto.getId());

        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldExceptionWhenGetAllItemRequestsAndSizeIsNegative() {
        UserDto userDto = userService.create(user1);
        UserDto requesterDto = userService.create(user2);

        RequestService.create(itemRequestDto, requesterDto.getId());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> RequestService.getAll(0, -1, userDto.getId()));
        assertEquals("Параметр size должен быть больше 0 или равен null!", exception.getMessage());
    }

    @Test
    void shouldExceptionWhenGetAllItemRequestsAndSizeIsZero() {
        UserDto userDto = userService.create(user1);
        UserDto requesterDto = userService.create(user2);

        RequestService.create(itemRequestDto, requesterDto.getId());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> RequestService.getAll(0, 0, userDto.getId()));
        assertEquals("Параметр size должен быть больше 0 или равен null!", exception.getMessage());
    }

    @Test
    void shouldExceptionWhenGetAllItemRequestsAndFromIsNegative() {
        UserDto userDto = userService.create(user1);
        UserDto requesterDto = userService.create(user2);

        RequestService.create(itemRequestDto, requesterDto.getId());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> RequestService.getAll(-1, null, userDto.getId()));
        assertEquals("Параметр from должен быть >= 0 или равен null!", exception.getMessage());
    }

    @Test
    void shouldReturnOwnItemRequests() {
        UserDto userDto = userService.create(user2);

        RequestService.create(itemRequestDto, userDto.getId());
        RequestService.create(itemRequestDto, userDto.getId());
        List<ItemRequestDto> listItemRequest = RequestService.getOwn(userDto.getId());

        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnItemRequestById() {
        UserDto userDto = userService.create(user1);

        ItemRequestDto newItemRequestDto = RequestService.create(itemRequestDto, userDto.getId());
        ItemRequestDto returnItemRequestDto = RequestService.getById(newItemRequestDto.getId(), userDto.getId());

        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}