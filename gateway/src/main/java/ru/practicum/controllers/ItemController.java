package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.ItemClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String REQ_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен POST запрос /items с телом {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestBody ItemDto itemDto,
                                           @PathVariable("itemId") Long itemId,
                                           @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен PATCH запрос /items/{} с телом {}", itemId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") Long itemId, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /items/{}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /items по пользователю {}", userId);
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam("text") String text, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен  GET запрос /items/search с телом {}", text);
        return itemClient.searchForItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestHeader(REQ_HEADER) Long userId) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
