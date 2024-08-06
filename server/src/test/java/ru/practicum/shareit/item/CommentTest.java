package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
public class CommentTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testCommentValid() {
        User author = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, author, 1L, List.of());
        Comment comment = new Comment(1L, "Test comment", item, author, LocalDateTime.now());

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        assertTrue(violations.isEmpty());
    }
}