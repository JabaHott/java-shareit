package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;

import java.util.List;

@Service
public interface BookingService {

    BookingDto create(BookingInDto bookingInDto, Long userId);

    BookingDto updateStatus(Long bookingId, Boolean approve, Long userid);

    BookingDto get(Long bookingId, Long userId);

    List<BookingDto> getAllBookingByUserId(Long userId, String state);

    List<BookingDto> getAllBookingsForUserItems(Long userId, String state);

}