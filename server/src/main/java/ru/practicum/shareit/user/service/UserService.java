package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    UserDto create(User user);

    UserDto update(User user, Long userId);

    UserDto get(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
