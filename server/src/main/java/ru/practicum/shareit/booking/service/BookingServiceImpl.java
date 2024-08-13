package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerValidationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Transactional
    @Override
    public BookingDto create(BookingInDto bookingInDto, Long userId) {
        User user = checkUserExistence(userId);
        Item item = checkItemExistence(bookingInDto);
        validate(bookingInDto, userId);

        Booking booking = mapper.toBooking(bookingInDto);
        booking.setStatus(WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        return mapper.toBookingDto(bookingRepository.save(booking));

    }

    @Transactional
    @Override
    public BookingDto updateStatus(Long bookingId, Boolean approve, Long userId) {
        Booking booking = checkBookingExistence(bookingId);

        if (!isOwner(userId, booking)) {
            String errorMessage = String.format("Статус бронирования может изменить только владелец вещи с id = %d!",
                    booking.getItem().getId());
            log.warn(errorMessage);
            throw new OwnerValidationException(errorMessage);
        }

        if (WAITING.equals(booking.getStatus()) && approve) {
            booking.setStatus(APPROVED);
        } else if (WAITING.equals(booking.getStatus())) {
            booking.setStatus(REJECTED);
        } else {
            String errorMessage = String.format("Невозможно изменить статус для бронирования с id = %d!", bookingId);
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }

        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto get(Long bookingId, Long userId) {

        checkUserExistence(userId);
        checkBookingExistence(bookingId);

        Booking booking = bookingRepository.getReferenceById(bookingId);

        if (!isBooker(userId, booking) && !isOwner(userId, booking)) {
            String errorMessage = String.format("У пользователя с id = %d нет прав для просмотра информации " +
                    "о бронировании с id = %d!", userId, booking.getId());
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return mapper.toBookingDto(bookingRepository.getReferenceById(bookingId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, String state) {
        checkUserExistence(userId);
        checkStateExistence(state);

        LocalDateTime dateTime = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (State.valueOf(state)) {
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, dateTime, dateTime,
                                sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndStatusAndEndIsBefore(userId, APPROVED, dateTime,
                                sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStatusInAndStartIsAfter(userId, List.of(APPROVED, WAITING),
                                dateTime, sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findByBookerId(userId, sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsForUserItems(Long userId, String state) {
        checkUserExistence(userId);
        checkStateExistence(state);

        LocalDateTime dateTime = LocalDateTime.now();
        switch (State.valueOf(state)) {
            case CURRENT:
                return bookingRepository.findAllBookingsByOwner(userId).stream()
                        .filter(booking -> booking.getStart().isBefore(dateTime) && booking.getEnd().isAfter(dateTime))
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(APPROVED)).stream()
                        .filter(booking -> booking.getEnd().isBefore(dateTime))
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(APPROVED, WAITING)).stream()
                        .filter(booking -> booking.getStart().isAfter(dateTime))
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(WAITING)).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(REJECTED)).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllBookingsByOwner(userId).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    private void checkStateExistence(String state) {
        var existingStates = Arrays.stream(State.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        if (!existingStates.contains(state)) {
            String errorMessage = String.format("Unknown state: %s", state);
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь c id = %d не найден!", userId);
                    log.warn(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    private Item checkItemExistence(BookingInDto bookingInputDto) {
        Long itemId = bookingInputDto.getItemId();
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Вещь с id = %d не найдена!", itemId);
                    log.warn(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    private Booking checkBookingExistence(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Бронирование с id = %d не найдено!", bookingId);
                    log.warn(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    private boolean isOwner(Long userId, Booking booking) {
        return itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId().equals(userId);
    }

    private boolean isBooker(Long userId, Booking booking) {
        return booking.getBooker().getId().equals(userId);
    }

    private void validate(BookingInDto bookingInputDto, Long userId) {
        Item item = itemRepository.getReferenceById(bookingInputDto.getItemId());

        if (Boolean.FALSE.equals(item.getAvailable())) {
            String errorMessage = String.format("Вещь с id = %d недоступна для бронирования!", bookingInputDto.getItemId());
            log.warn(errorMessage);
            throw new NotAvailableBookingException(errorMessage);
        }

        if (itemRepository.getReferenceById(bookingInputDto.getItemId()).getOwner().getId().equals(userId)) {
            String errorMessage = "Вы не можете забронировать вещь, для которой являетесь владельцем!";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}