package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInDtoTest {

    private final JacksonTester<BookingInDto> json;
    private BookingInDto BookingInDto;
    private final Validator validator;

    public BookingInDtoTest(@Autowired JacksonTester<BookingInDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        BookingInDto = new BookingInDto(
                1L,
                LocalDateTime.of(2030, 12, 25, 12, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0));
    }

    @Test
    void testJsonBookingInDto() throws Exception {
        JsonContent<BookingInDto> result = json.write(BookingInDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-12-26T12:00:00");
    }

    @Test
    void whenBookingInDtoIsValidThenViolationsShouldBeEmpty() {
        Set<ConstraintViolation<BookingInDto>> violations = validator.validate(BookingInDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenBookingInDtoItemIdNotNullThenViolationsShouldBeReportedNotNull() {
        BookingInDto.setItemId(null);
        Set<ConstraintViolation<BookingInDto>> violations = validator.validate(BookingInDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Не указан id вещи (itemId)!");
    }

    @Test
    void whenBookingInDtoStartNotNullThenViolationsShouldBeReportedNotNull() {
        BookingInDto.setStart(null);
        Set<ConstraintViolation<BookingInDto>> violations = validator.validate(BookingInDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Не указана дата и время начала бронирования (start)!");
    }

    @Test
    void whenBookingInDtoEndNotNullThenViolationsShouldBeReportedNotNull() {
        BookingInDto.setEnd(null);
        Set<ConstraintViolation<BookingInDto>> violations = validator.validate(BookingInDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Не указана дата и время окончания бронирования (end)!");
    }

    @Test
    void whenBookingInDtoStartBeforeNowThenViolationsShouldBeReportedNotNull() {
        BookingInDto.setStart(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingInDto>> violations = validator.validate(BookingInDto);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Дата начала бронирования (start) должна содержать сегодняшнее " +
                "число или дату, которая ещё не наступила!");
    }

    @Test
    void whenBookingInDtoEndBeforeNowThenViolationsShouldBeReportedNotNull() {
        BookingInDto.setEnd(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingInDto>> violations = validator.validate(BookingInDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Дата окончания бронирования (end) должна содержать дату, которая " +
                "ещё не наступила!");
    }
}