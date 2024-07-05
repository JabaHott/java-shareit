package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(User user) {
        try {
            log.debug("Создан пользователь с id = {} и почтой {}", user.getId(), user.getEmail());
            return userMapper.toUserDto(userRepository.save(user));
        } catch (Exception e) {
            String error = String.format("Указанная почта " + user.getEmail() + " уже привязана к аккаунту!");
            log.warn(error);
            throw new EmailDuplicateException(error);
        }
    }

    @Transactional
    @Override
    public UserDto update(User user, Long userId) {
        userRepository.existsById(userId);
        if (userRepository.existsByEmail(user.getEmail()) && !get(userId).getEmail().equals(user.getEmail())) {
            String error = String.format("Указанная почта " + user.getEmail() + " уже привязана к аккаунту!");
            log.warn(error);
            throw new EmailDuplicateException(error);
        }
        User oldUser = userRepository.getReferenceById(userId);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        log.debug("Изменен пользователь с id = {} и почтой {}", userId, user.getEmail());
        return userMapper.toUserDto(userRepository.save(oldUser));
    }

    @Transactional
    @Override
    public UserDto get(Long id) {
        if (!userRepository.existsById(id)) {
            String error = String.format("Указанный пользователь " + id + " еще не создан!");
            log.warn(error);
            throw new NotFoundException(error);
        }
        log.debug("Изменен пользователь с id = {}", id);
        return userMapper.toUserDto(userRepository.getReferenceById(id));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.debug("Удален пользователь с id = {}", id);
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        log.debug("Запрошен список всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
