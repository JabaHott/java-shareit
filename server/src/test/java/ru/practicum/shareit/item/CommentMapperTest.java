package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    public void setUp() {
        commentMapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    public void testToCommentDto() {
        // Создаем объекты User и Item
        User author = new User();
        author.setName("Test User");
        Item item = new Item();
        item.setId(1L);


        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setAuthor(author);
        comment.setItem(item);


        CommentDto commentDto = commentMapper.toCommentDto(comment);


        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(author.getName(), commentDto.getAuthorName());
    }

    @Test
    public void testToComment() {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test Comment DTO");
        commentDto.setAuthorName("Test User");


        Comment comment = commentMapper.toComment(commentDto);


        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(null, comment.getAuthor());
        assertEquals(null, comment.getItem());
    }
}