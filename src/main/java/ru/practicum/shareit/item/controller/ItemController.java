package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.*;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private static final String REQ_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен POST запрос /items с телом {}", itemDto);
        Item item = itemMapper.toItem(itemDto);
        return ResponseEntity.ok().body(itemService.create(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@RequestBody ItemDto itemDto,
                                            @PathVariable("itemId") Long itemId,
                                            @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен PATCH запрос /items/{} с телом {}", itemId, itemDto);
        Item item = itemMapper.toItem(itemDto);
        return ResponseEntity.ok().body(itemService.update(item, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable("itemId") Long itemId) {
        log.info("Получен GET запрос /items/{}", itemId);
        return ResponseEntity.ok().body(itemService.get(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /items по пользователю {}", userId);
        return ResponseEntity.ok().body(itemService.getAll(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text) {
        log.info("Получен  GET запрос /items/search с телом {}", text);
        return ResponseEntity.ok().body(itemService.search(text));
    }
}
