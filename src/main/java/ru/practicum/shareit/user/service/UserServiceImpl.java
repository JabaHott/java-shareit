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
        return userMapper.toUserDto(userDao.save(user));

    }

    @Override
    public UserDto update(User user, Long userId) {
        if (idValidation(user.getId())) {
            //throw new ValidationException("Указанный пользователь " + user.getId() + " еще не создан!");
        }
        if (emailValidation(user.getEmail()) && !userDao.get(userId).getEmail().equals(user.getEmail())) {
            throw new EmailDuplicateException("Указанная почта " + user.getEmail() + " уже привязана к аккаунту!");
        }
        return userMapper.toUserDto(userDao.update(user, userId));
    }

    @Override
    public UserDto get(Long id) {
        if (idValidation(id)) {
            throw new NotFoundException("Указанный пользователь " + id + " еще не создан!");
        }
        return userMapper.toUserDto(userDao.get(id));
    }

    @Override
    public List<UserDto> getAll() {
        return userDao.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (idValidation(id)) {
            //throw new ValidationException("Указанный пользователь " + id + " еще не создан!");
        }
        userDao.delete(id);
    }

    @Override
    public List<String> getAllEmails() {
        return userDao.getAllEmails();
    }

    @Override
    public List<Long> getAllIds() {
        return userDao.getAllIds();
    }

    private boolean emailValidation(String email) {
        return userDao.getAllEmails().contains(email);
    }

    private boolean idValidation(Long id) {
        return !(userDao.getAllIds().contains(id));
    }
}
