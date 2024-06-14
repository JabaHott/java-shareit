package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Validated
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST запрос /users с телом {}", userDto);
        User user = userMapper.toUser(userDto);
        return ResponseEntity.ok().body(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable("userId") Long userId) {
        log.info("Получен PATCH запрос /users/{} с телом {}", userId, userDto);
        User user = userMapper.toUser(userDto);
        return ResponseEntity.ok().body(userService.update(user, userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") Long userId) {
        log.info("Получен GET запрос /users/{}", userId);
        return ResponseEntity.ok().body(userService.get(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Получен GET запрос /users");
        return ResponseEntity.ok().body(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен DELETE запрос /users/{}", userId);
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }

}