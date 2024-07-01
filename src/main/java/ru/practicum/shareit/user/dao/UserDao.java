package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface UserDao {
    User save(User user);

    User update(User user, Long userId);

    User get(Long id);

    Collection<User> getAll();

    void delete(Long id);

    List<String> getAllEmails();

    List<Long> getAllIds();
}
