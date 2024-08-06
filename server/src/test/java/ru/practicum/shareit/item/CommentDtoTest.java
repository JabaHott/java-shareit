package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentDtoTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testCommentDtoValid() {
        CommentDto commentDto = new CommentDto(1L, "Test comment", "John Doe", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testCommentDtoInvalidText() {
        CommentDto commentDto = new CommentDto(1L, "", "John Doe", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testCommentDtoNullText() {
        CommentDto commentDto = new CommentDto(1L, null, "John Doe", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testCommentDtoBlankText() {
        CommentDto commentDto = new CommentDto(1L, "   ", "John Doe", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testCommentDtoValidText() {
        CommentDto commentDto = new CommentDto(1L, "Test comment", null, LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertTrue(violations.isEmpty());
    }
}