package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(requestService.create(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getMyItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId)  {
        return ResponseEntity.ok().body(requestService.getOwn(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests(@RequestParam(value = "from", required = false) Integer from,
                                                                   @RequestParam(value = "size", required = false) Integer size,
                                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(requestService.getAll(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@PathVariable("requestId") Long requestId,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(requestService.getById(requestId, userId));
    }

}
