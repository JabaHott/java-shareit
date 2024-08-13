package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.UserClient;
import ru.practicum.dto.UserDto;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    @Validated({BaseControllerInterface.Create.class})
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        log.info("Получен POST запрос /users с телом {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @Validated({BaseControllerInterface.Update.class})
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable("userId") Long userId) {
        log.info("Получен PATCH запрос /users/{} с телом {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
        log.info("Получен GET запрос /users/{}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен GET запрос /users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен DELETE запрос /users/{}", userId);
        userClient.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

}