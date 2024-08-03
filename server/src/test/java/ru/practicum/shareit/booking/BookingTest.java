package ru.practicum.shareit.booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingTest {

    @Test
    void testBookingCreation() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Booking booking = new Booking(1L, start, end, item, booker, Status.APPROVED);

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void testBookingToString() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking = new Booking(1L, start, end, item, booker, Status.APPROVED);

        String toString = booking.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Booking{"));
        assertTrue(toString.contains("status=" + Status.APPROVED));
        assertTrue(toString.contains("end=" + end));
        assertTrue(toString.contains("start=" + start));
        assertTrue(toString.contains("id=" + 1L));
    }

    @Test
    void testBookingEquals() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking1 = new Booking(1L, start, end, item, booker, Status.APPROVED);
        Booking booking2 = new Booking(1L, start, end, item, booker, Status.APPROVED);

        boolean equals = booking1.equals(booking2);

        assertTrue(equals);
    }

    @Test
    void testBookingNotEquals() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking1 = new Booking(1L, start, end, item, booker, Status.APPROVED);
        Booking booking2 = new Booking(2L, start, end, item, booker, Status.APPROVED);

        boolean equals = booking1.equals(booking2);

        assertFalse(equals);
    }

    @Test
    void testBookingHashCode() {
        User booker = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Test item", "Test description", true, booker, 1L, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking = new Booking(1L, start, end, item, booker, Status.APPROVED);

        int hashCode = booking.hashCode();

        assertNotNull(hashCode);
    }
}