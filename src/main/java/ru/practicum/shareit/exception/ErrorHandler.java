package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.user.controller.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailDuplicateException(final EmailDuplicateException e) {
        log.error("Попытка создать пользователя с уже существующей постой. Ответ: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(javax.validation.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.warn(e.getFieldErrors().get(0).getDefaultMessage());
        return new ErrorResponse(e.getFieldErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.error("В запросе не был передан обязательный заголовок {}!", e.getHeaderName());
        return new ErrorResponse(String.format("В запросе не был передан обязательный заголовок %s!",
                e.getHeaderName()));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomValidationException(ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(OwnerValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerValidationException(OwnerValidationException e) {
        log.error("Ошибка доступа. Ответ: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
