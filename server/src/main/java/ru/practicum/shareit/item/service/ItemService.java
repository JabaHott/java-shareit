package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long itemId, Long userId);

    ItemDto get(Long id, Long userid);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
