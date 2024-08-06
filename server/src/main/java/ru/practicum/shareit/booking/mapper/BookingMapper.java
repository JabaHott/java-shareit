package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    BookingWithBookerIdDto toBookingWithBookerIdDto(Booking booking);

    Booking toBooking(BookingInDto bookingInDto);
}
