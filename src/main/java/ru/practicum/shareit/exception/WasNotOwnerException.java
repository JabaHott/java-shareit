package ru.practicum.shareit.exception;

public class WasNotOwnerException extends RuntimeException {
    public WasNotOwnerException(String message) {
        super(message);
    }
}