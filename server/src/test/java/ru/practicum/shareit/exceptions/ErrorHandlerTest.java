package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @Mock
    private NotFoundException notFoundException;

    @Mock
    private EmailDuplicateException emailDuplicateException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private ValidationException validationException;

    @Mock
    private WasNotOwnerException wasNotOwnerException;

    @Mock
    private ru.practicum.shareit.exception.notAvailableBookingException notAvailableBookingException;

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleBookingNotFoundException() {
        when(notFoundException.getMessage()).thenReturn("not found");
        ErrorResponse result = errorHandler.handleNotFoundException(notFoundException);
        assertEquals("not found", result.getError());
    }

    @Test
    void handleEmailDuplicateException() {
        when(emailDuplicateException.getMessage()).thenReturn("Email already exists");
        ErrorResponse result = errorHandler.handleEmailDuplicateException(emailDuplicateException);
        assertEquals("Email already exists", result.getError());
    }

    @Test
    void handleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("fieldName", "fieldName", "error message");
        when(methodArgumentNotValidException.getFieldErrors()).thenReturn(List.of(fieldError));
        ErrorResponse result = errorHandler.handleValidationException(methodArgumentNotValidException);
        assertEquals("error message", result.getError());
    }

    @Test
    void handleValidationException() {
        when(validationException.getMessage()).thenReturn("Validation failed");
        ErrorResponse result = errorHandler.handleCustomValidationException(validationException);
        assertEquals("Validation failed", result.getError());
    }

    @Test
    void handleNotEnoughRightsException() {
        when(wasNotOwnerException.getMessage()).thenReturn("Insufficient rights");
        ErrorResponse result = errorHandler.handleWasNotOwnerException(wasNotOwnerException);
        assertEquals("Insufficient rights", result.getError());
    }


    @Test
    void handleNotAvailableForBookingException() {
        when(notAvailableBookingException.getMessage()).thenReturn("Item not available for booking");
        ErrorResponse result = errorHandler.handleNotAvailableBookingException(notAvailableBookingException);
        assertEquals("Item not available for booking", result.getError());
    }
}