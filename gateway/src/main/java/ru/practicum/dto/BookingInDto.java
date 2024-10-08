package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.errors.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingInDto {
    @NotNull(message = "Не указан id вещи (itemId)!")
    private Long itemId;
    @NotNull(message = "Не указана дата и время начала бронирования (start)!")
    @FutureOrPresent(message = "Дата начала бронирования (start) должна содержать сегодняшнее число или дату, которая " +
            "ещё не наступила!")
    private LocalDateTime start;
    @NotNull(message = "Не указана дата и время окончания бронирования (end)!")
    @Future(message = "Дата окончания бронирования (end) должна содержать дату, которая ещё не наступила!")
    private LocalDateTime end;
}
