package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class UserDtoTest {

    private final Validator validator;
    private final JacksonTester<UserDto> json;
    private UserDto userDto;

    public UserDtoTest(@Autowired JacksonTester<UserDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "name", "email@yandex.ru");
    }

    @Test
    void testJsonItemDto() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@yandex.ru");
    }

    @Test
    void whenUserDtoIsValidThenViolationsShouldBeEmpty() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsBlankThenViolationsShouldBeReportedNotBlank() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenEmailIsBlankThenViolationsShouldBeReportedNotBlank() {
        userDto.setEmail(" ");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("должно иметь формат адреса электронной почты");
    }

    @Test
    void whenEmailIsNotValidThenViolationsShouldBeReportedNotBlank() {
        userDto.setEmail("email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("должно иметь формат адреса электронной почты");
    }

}
