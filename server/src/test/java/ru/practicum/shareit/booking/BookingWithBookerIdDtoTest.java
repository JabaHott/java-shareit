package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingWithBookerIdDtoTest {

    private final JacksonTester<BookingWithBookerIdDto> json;

    public BookingWithBookerIdDtoTest(@Autowired JacksonTester<BookingWithBookerIdDto> json) {
        this.json = json;
    }

    @Test
    void testJsonBookingShortDto() throws Exception {
        BookingWithBookerIdDto booking = new BookingWithBookerIdDto();
        booking.setId(1L);
        booking.setBookerId(2L);
        booking.setStartTime(LocalDateTime.of(2030, 12, 25, 12, 0, 0));
        booking.setEndTime(LocalDateTime.of(2030, 12, 25, 14, 0, 0));

        JsonContent<BookingWithBookerIdDto> result = json.write(booking);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1); // Проверка значения id
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.startTime").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.endTime").isEqualTo("2030-12-25T14:00:00");
    }
}