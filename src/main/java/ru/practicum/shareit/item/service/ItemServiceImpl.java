package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Item item, Long userId) {
        UserDto user = userService.get(userId);
        item.setOwnerId(userId);
        log.debug("Создан товар с id = {} и id хозяина = {}", item.getId(), userId);
        return itemMapper.toItemDto(itemDao.save(item));
    }

    @Override
    public ItemDto update(Item item, Long itemId, Long userId) {
        UserDto userDto = userService.get(userId);
        if (!userId.equals(itemDao.get(itemId).getOwnerId())) {
            String errorMessage = String.format("Пользователь c id = %d не является владельцем вещи с id = %d!", userId,
                    itemId);
            log.warn(errorMessage);
            throw new OwnerValidationException(errorMessage);
        }
        log.debug("Изменен товар с id = {} и id хозяина = {}", itemId, userId);
        return itemMapper.toItemDto(itemDao.update(item, itemId));
    }

    @Override
    public ItemDto get(Long id) {
        Item item = itemDao.get(id);
        if (item == null) {
            String errorMessage = String.format("Вещь с id = %d не найдена!", id);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.debug("Получен товар с id = {}", id);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        log.debug("Получен список всех товаров");
        return itemDao.getAllByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        log.debug("Запрошен список товаров");
        return itemDao.search(text.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getIdsList() {
        log.debug("Запрошен список всех идентификаторов");
        return itemDao.getIdsList();
    }

}
