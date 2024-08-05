package ru.practicum.errors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.controllers.BookingController;
import ru.practicum.controllers.ItemController;
import ru.practicum.controllers.RequestController;
import ru.practicum.controllers.UserController;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class, RequestController.class})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraint(ConstraintViolationException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }
}
