package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testItemValid() {

        User owner = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test name", "Test description", true, owner, 1L, List.of());

        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        assertTrue(violations.isEmpty());
    }


    @Test
    void testEqualsAndHashCode() {

        User owner = new User(1L, "John Doe", "john.doe@example.com");
        Item item1 = new Item(1L, "Test name", "Test description", true, owner, 1L, List.of());
        Item item2 = new Item(1L, "Test name", "Test description", true, owner, 1L, List.of());


        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testToString() {

        User owner = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test name", "Test description", true, owner, 1L, List.of());

        assertNotNull(item.toString());
        assertTrue(item.toString().contains("Item"));
        assertTrue(item.toString().contains("id=1"));
        assertTrue(item.toString().contains("name='Test name'"));
        assertTrue(item.toString().contains("description='Test description'"));
        assertTrue(item.toString().contains("available=true"));
        assertTrue(item.toString().contains("requestId=1"));
    }
}