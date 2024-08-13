package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class BookingMapperTest {

    private BookingMapper bookingMapper;
    private UserMapper userMapper;
    private ItemMapper itemMapper;

    @BeforeEach
    public void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        itemMapper = Mappers.getMapper(ItemMapper.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    void testToBookingWithBookerIdDto() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, Status.APPROVED);

        BookingWithBookerIdDto bookingWithBookerIdDto = bookingMapper.toBookingWithBookerIdDto(booking);

        assertNotNull(bookingWithBookerIdDto);
        assertEquals(booking.getId(), bookingWithBookerIdDto.getId());
        assertEquals(booking.getBooker().getId(), bookingWithBookerIdDto.getBookerId());
    }

    @Test
    void testToBooking() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Booking booking = bookingMapper.toBooking(bookingInDto);

        assertNotNull(booking);
        assertEquals(bookingInDto.getStart(), booking.getStart());
        assertEquals(bookingInDto.getEnd(), booking.getEnd());
    }
}