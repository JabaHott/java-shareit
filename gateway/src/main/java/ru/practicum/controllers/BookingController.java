package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.BookingClient;
import ru.practicum.dto.State;
import ru.practicum.dto.BookingInDto;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String REQ_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingInDto bookingInDto,
                                         @RequestHeader(REQ_HEADER) Long bookerId) {
        log.info("Получен POST запрос /bookings c телом {}, пользователь - {}", bookingInDto, bookerId);
        return bookingClient.create(bookingInDto, bookerId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                                         @RequestHeader(REQ_HEADER) Long userId, @RequestParam Boolean approved) {
        log.info("Получен PATCH запрос /bookings/bookingId бронь={}, пользователь={}, состояние={}", bookingId, userId, approved);
        return bookingClient.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId, @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /bookings' на получение бронирования с ID={}", bookingId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /bookings бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        State stateEnum = State.from(state).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getAllBookingByUserId(userId, stateEnum);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestHeader(REQ_HEADER) Long userId) {
        log.info("Получен GET запрос /bookings/owner бронирований вещей пользователя с ID={} с параметром STATE={}",
                userId, state);
        State stateEnum = State.from(state).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getBookingForUserItems(userId, stateEnum);
    }
}


