package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testItemRequestValid() {
        User requester = new User(1L, "John Doe", "john.doe@example.com");
        ItemRequest request = new ItemRequest(1L, "Test description", requester, LocalDateTime.now(), List.of());

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testItemRequestInvalidDescription() {
        User requester = new User(1L, "John Doe", "john.doe@example.com");
        ItemRequest request = new ItemRequest(1L, "", requester, LocalDateTime.now(), List.of());

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testItemRequestInvalidCreatedDate() {
        User requester = new User(1L, "John Doe", "john.doe@example.com");
        ItemRequest request = new ItemRequest(1L, "Test description", requester, null, List.of());

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("created", violations.iterator().next().getPropertyPath().toString());
    }
}