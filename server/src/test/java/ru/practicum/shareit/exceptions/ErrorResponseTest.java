package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        String errorMessage = "Тестовое сообщение о ошибке";

        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        assertEquals(errorMessage, errorResponse.getError());
    }
}