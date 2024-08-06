package ru.practicum.shareit.request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {

    private RequestMapper requestMapper;

    @BeforeEach
    public void setUp() {
        requestMapper = Mappers.getMapper(RequestMapper.class);
    }

    @Test
    public void testToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test Request");


        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    public void testToItemRequest() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test Request DTO");


        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);


        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }
}