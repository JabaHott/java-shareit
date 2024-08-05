package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.RequestClient;
import ru.practicum.dto.RequestDto;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;
    private static final String REQ_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody RequestDto requestDto,
                                                    @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Пользователь с id = {} отправил запрос на создание запроса", userId);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(REQ_HEADER) Long userId) {
        log.info("Пользователь с id = {} отправил запрос на получение оставленных им запросов", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestParam(value = "from", required = false) Integer from,
                                                     @RequestParam(value = "size", required = false) Integer size,
                                                     @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Пользователь с id = {} отправил запрос на получение всех запросов (from = {}, size = {}", userId,
                from, size);
        return requestClient.getAllIRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("requestId") Long requestId,
                                                     @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Пользователь с id = {} отправил запрос на получение запроса с id = {}", userId, requestId);
        return requestClient.getRequest(userId, requestId);
    }
}