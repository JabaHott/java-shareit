package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {

    private final Map<Long, User> users;
    private final List<String> emails;
    private Long idCounter = 1L;

    public UserDaoImpl() {
        this.users = new HashMap<>();
        this.emails = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        User oldUser = users.get(userId);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            emails.remove(oldUser.getEmail());
            oldUser.setEmail(user.getEmail());
            emails.add(user.getEmail());
        }
        return oldUser;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void delete(Long id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<String> getAllEmails() {
        return emails;
    }

    @Override
    public List<Long> getAllIds() {
        return users.values().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
