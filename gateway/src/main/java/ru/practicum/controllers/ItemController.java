package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.ItemClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import java.util.Collections;


@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String REQ_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @Validated({BaseControllerInterface.Create.class})
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен POST запрос /items с телом {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated({BaseControllerInterface.Update.class})
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
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String text, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен  GET запрос /items/search с телом {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchForItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    @Validated(BaseControllerInterface.Create.class)
    public ResponseEntity<Object> addComment(@RequestBody CommentDto commentDto,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestHeader(REQ_HEADER) Long userId) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
