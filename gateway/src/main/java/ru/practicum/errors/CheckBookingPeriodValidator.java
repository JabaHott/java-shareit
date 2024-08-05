package ru.practicum.errors;

import ru.practicum.dto.BookingInDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckBookingPeriodValidator implements ConstraintValidator<BookingValidation, BookingInDto> {

    @Override
    public void initialize(BookingValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingInDto bookingInDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingInDto.getStart();
        LocalDateTime end = bookingInDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
