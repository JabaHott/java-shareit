package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        // Инициализация маппера
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    public void shouldMapUserToUserDto() {
        // Создание тестового объекта User
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Выполнение маппинга
        UserDto userDto = userMapper.toUserDto(user);

        // Проверка корректности маппинга
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldMapUserDtoToUser() {
        // Создание тестового объекта UserDto
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        // Выполнение маппинга
        User user = userMapper.toUser(userDto);

        // Проверка корректности маппинга
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }
}