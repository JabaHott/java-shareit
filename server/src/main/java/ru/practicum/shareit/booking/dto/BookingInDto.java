package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInDto {
    @NotNull(message = "Не указан id вещи (itemId)!")
    private Long itemId;

    @NotNull(message = "Не указана дата и время начала бронирования (start)!")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @FutureOrPresent(message = "Дата начала бронирования (start) должна содержать сегодняшнее число или дату, которая " +
            "ещё не наступила!")
    private LocalDateTime start;

    @NotNull(message = "Не указана дата и время окончания бронирования (end)!")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @Future(message = "Дата окончания бронирования (end) должна содержать дату, которая ещё не наступила!")
    private LocalDateTime end;
}
