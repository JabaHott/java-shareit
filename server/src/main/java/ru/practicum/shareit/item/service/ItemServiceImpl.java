package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerValidationException;
import ru.practicum.shareit.exception.WasNotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.getReferenceById(userId));
        log.debug("Создан товар с id = {} и id хозяина = {}", item.getId(), userId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        checkUser(userId);
        checkItem(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            String errorMessage = String.format("Пользователь c id = %d не является владельцем вещи с id = %d!", userId,
                    itemId);
            log.warn(errorMessage);
            throw new OwnerValidationException(errorMessage);
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.debug("Изменен товар с id = {} и id хозяина = {}", itemId, userId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto get(Long id, Long userId) {
        checkItem(id);
        Item item = itemRepository.getReferenceById(id);
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (itemRepository.getReferenceById(id).getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(bookingMapper.toBookingWithBookerIdDto(getLastBooking(id, LocalDateTime.now())));
            itemDto.setNextBooking(bookingMapper.toBookingWithBookerIdDto(getNextBooking(id, LocalDateTime.now())));
        }
        log.debug("Получен товар с id = {}", id);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> allItems = itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());

        LocalDateTime dateTime = LocalDateTime.now();

        for (ItemDto itemDto : allItems) {
            Booking lastBooking = getLastBooking(itemDto.getId(), dateTime);
            Booking nextBooking = getNextBooking(itemDto.getId(), dateTime);

            itemDto.setLastBooking(lastBooking != null ? bookingMapper.toBookingWithBookerIdDto(lastBooking) : null);
            itemDto.setNextBooking(nextBooking != null ? bookingMapper.toBookingWithBookerIdDto(nextBooking) : null);

            itemDto.setComments(commentRepository.findCommentsByItemId(itemDto.getId()).stream()
                    .map(commentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        }

        return allItems;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        log.debug("Запрошен список товаров");
        return itemRepository.search(text.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        checkUser(userId);
        checkItem(itemId);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findPastAndCurrentActiveBookingsByBookerIdAndItemId(userId, itemId,
                dateTime);
        if (bookings.isEmpty()) {
            String error = String.format("Пользователь с id = %d никогда не бронировал вещь с id = %d!", userId,
                    itemId);
            log.warn(error);
            throw new WasNotOwnerException(error);
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(itemRepository.getReferenceById(itemId));
        comment.setAuthor(userRepository.getReferenceById(userId));
        comment.setCreated(dateTime);

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            String error = String.format("Указанный пользователь " + userId + " не найден!");
            log.warn(error);
            throw new NotFoundException(error);
        }
    }

    private void checkItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            String error = String.format("Указанный товар " + itemId + " не найден!");
            log.warn(error);
            throw new NotFoundException(error);
        }
    }

    private Booking getLastBooking(Long itemId, LocalDateTime dateTime) {
        List<Booking> approvedItemBookings = bookingRepository.findAllBookingsByItemId(itemId).stream()
                .filter(booking -> APPROVED.equals(booking.getStatus()))
                .collect(Collectors.toList());

        return approvedItemBookings.stream()
                .filter(booking -> booking.getStart().isBefore(dateTime))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking getNextBooking(Long itemId, LocalDateTime dateTime) {
        List<Booking> approvedItemBookings = bookingRepository.findAllBookingsByItemId(itemId).stream()
                .filter(booking -> APPROVED.equals(booking.getStatus()))
                .collect(Collectors.toList());

        return approvedItemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(dateTime))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
