package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingWithBookerIdDto {
    private Long id;

    private Long bookerId;
    @FutureOrPresent
    @NotNull
    private LocalDateTime startTime;
    @Future
    @NotNull
    private LocalDateTime endTime;
}
