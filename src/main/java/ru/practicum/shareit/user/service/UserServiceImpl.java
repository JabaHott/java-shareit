package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserDto create(User user) {
        if (emailValidation(user.getEmail())) {
            throw new EmailDuplicateException("Указанная почта " + user.getEmail() + " уже привязана к аккаунту!");
        }
        log.debug("Создан пользователь с id = {} и почтой {}", user.getId(), user.getEmail());
        return userMapper.toUserDto(userDao.save(user));

    }

    @Override
    public UserDto update(User user, Long userId) {
        if (emailValidation(user.getEmail()) && !userDao.get(userId).getEmail().equals(user.getEmail())) {
            throw new EmailDuplicateException("Указанная почта " + user.getEmail() + " уже привязана к аккаунту!");
        }
        log.debug("Изменен пользователь с id = {} и почтой {}", userId, user.getEmail());
        return userMapper.toUserDto(userDao.update(user, userId));
    }

    @Override
    public UserDto get(Long id) {
        if (idValidation(id)) {
            throw new NotFoundException("Указанный пользователь " + id + " еще не создан!");
        }
        log.debug("Изменен пользователь с id = {}", id);
        return userMapper.toUserDto(userDao.get(id));
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("Запрошен список всех пользователей");
        return userDao.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.debug("Удален пользователь с id = {}", id);
        userDao.delete(id);
    }

    @Override
    public List<String> getAllEmails() {
        log.debug("Получен список всех почт");
        return userDao.getAllEmails();
    }

    @Override
    public List<Long> getAllIds() {
        log.debug("Получен список всех идентификаторов");
        return userDao.getAllIds();
    }

    private boolean emailValidation(String email) {
        return userDao.getAllEmails().contains(email);
    }

    private boolean idValidation(Long id) {
        return !(userDao.getAllIds().contains(id));
    }
}
