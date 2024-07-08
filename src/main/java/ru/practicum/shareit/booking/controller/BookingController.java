package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String REQ_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @ResponseBody
    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingInDto bookingInDto,
                             @RequestHeader(REQ_HEADER) Long bookerId) {
        log.info("Получен POST запрос /bookings c телом {}, пользователь - {}", bookingInDto, bookerId);
        return bookingService.create(bookingInDto, bookerId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(REQ_HEADER) Long userId, @RequestParam Boolean approved) {
        log.info("Получен PATCH запрос /bookings/bookingId бронь={}, пользователь={}, состояние={}", bookingId, userId, approved);
        return bookingService.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /bookings' на получение бронирования с ID={}", bookingId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /bookings бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        return bookingService.getAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /bookings/owner бронирований вещей пользователя с ID={} с параметром STATE={}",
                userId, state);
        return bookingService.getAllBookingsForUserItems(userId, state);
    }
}


