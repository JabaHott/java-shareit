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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        checkUser(userId);
        checkItemExistence(bookingInDto);
        checkBookingClause(bookingInDto, userId);

        Booking booking = mapper.toBooking(bookingInDto);
        booking.setStatus(Status.WAITING);
        booking.setItem(itemRepository.getReferenceById(bookingInDto.getItemId()));
        booking.setBooker(userRepository.getReferenceById(userId));

        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto updateStatus(Long bookingId, Boolean approve, Long userId) {
        bookingExistenceCheck(bookingId);
        checkUser(userId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (isOwner(userId, booking)) {
            String error = String.format("Пользователь с id = %d не владеет товаром!", userId);
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (Status.WAITING.equals(booking.getStatus()) && approve) {
            booking.setStatus(Status.APPROVED);
        } else if (Status.WAITING.equals(booking.getStatus())) {
            booking.setStatus(Status.REJECTED);
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
        bookingExistenceCheck(bookingId);
        checkUser(userId);

        Booking booking = bookingRepository.getReferenceById(bookingId);

        if (!booking.getBooker().getId().equals(userId) && isOwner(userId, booking)) {
            String errorMessage = String.format("У пользователя с id = %d нет прав для просмотра информации " +
                    "о бронировании с id = %d!", userId, booking.getId());
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return mapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, String state) {
        checkUser(userId);
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
                return bookingRepository.findByBookerIdAndStatusAndEndIsBefore(userId, Status.APPROVED, dateTime, sort).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStatusInAndStartIsAfter(userId, List.of(Status.APPROVED, Status.WAITING),
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
        checkUser(userId);
        checkStateExistence(state);

        LocalDateTime dateTime = LocalDateTime.now();

        switch (State.valueOf(state)) {
            case CURRENT:
                return bookingRepository.findAllBookingsByOwner(userId).stream()
                        .filter(booking -> booking.getStart().isBefore(dateTime) && booking.getEnd().isAfter(dateTime))
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(Status.APPROVED)).stream()
                        .filter(booking -> booking.getEnd().isBefore(dateTime))
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(Status.APPROVED, Status.WAITING)).stream()
                        .filter(booking -> booking.getStart().isAfter(dateTime))
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(Status.WAITING)).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(Status.REJECTED)).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllBookingsByOwner(userId).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            String error = String.format("Пользователь с id = %d не найден!", userId);
            log.warn(error);
            throw new NotFoundException(error);
        }
    }

    private void checkBookingClause(BookingInDto bookingInDto, Long userId) {
        Item item = itemRepository.getReferenceById(bookingInDto.getItemId());

        if (!item.getAvailable()) {
            String error = String.format("Вещь с id = %d недоступна для бронирования!", bookingInDto.getItemId());
            log.warn(error);
            throw new NotAvailableBookingException(error);
        }

        if (item.getOwner().getId().equals(userId)) {
            String error = "Нельзя бронировать свою вещь!";
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (bookingInDto.getEnd().isBefore(bookingInDto.getStart())
                || bookingInDto.getEnd().equals(bookingInDto.getStart())) {
            String error = "Дата окончания бронирования не может быть раньше или равняться дате начала бронирования";
            log.warn(error);
            throw new ValidationException(error);
        }
    }

    private void bookingExistenceCheck(Long id) {
        if (!bookingRepository.existsById(id)) {
            String error = String.format("Бронирование с id = %d не найден!", id);
            log.warn(error);
            throw new NotFoundException(error);
        }
    }

    private void checkItemExistence(BookingInDto bookingInputDto) {
        if (!itemRepository.existsById(bookingInputDto.getItemId())) {
            String errorMessage = String.format("Вещь с id = %d не найдена!", bookingInputDto.getItemId());
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    private boolean isOwner(Long userId, Booking booking) {
        return !itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId().equals(userId);
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


}