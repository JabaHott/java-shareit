package ru.practicum.shareit.exception;

public class notAvailableBookingException extends RuntimeException {
    public notAvailableBookingException(String message) {
        super(message);
    }
}
