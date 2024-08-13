package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerValidationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private final User user1 = new User(300L, "user1", "first@user.ru");
    private final User user2 = new User(301L, "user2", "second@user.ru");
    private final User user3 = new User(302L, "user3", "third@user.ru");

    private final ItemDto itemDto1 = new ItemDto(301L, "item1", "description1", true, null);

    @Test
    void shouldExceptionWhenCreateBookingByOwnerItem() {
        UserDto ownerDto = userService.create(user1);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInDto, ownerDto.getId()));
        assertEquals("Вы не можете забронировать вещь, для которой являетесь владельцем!", exp.getMessage());
    }

    @Test
    void shouldExceptionWhenUpdateStatusByBooker() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());

        OwnerValidationException exp = assertThrows(OwnerValidationException.class,
                () -> bookingService.updateStatus(bookingDto.getId(), true, bookerDto.getId()));
        assertEquals(String.format("Статус бронирования может изменить только владелец вещи с id = %d!", itemDto.getId()), exp.getMessage());
    }

    @Test
    void shouldSetStatusToApprovedWhenUpdateStatusByOwnerAndApprovedTrue() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());
        bookingDto = bookingService.updateStatus(bookingDto.getId(), true, ownerDto.getId());

        assertEquals(Status.APPROVED, bookingDto.getStatus());
    }

    @Test
    void shouldSetStatusToRejectedWhenUpdateStatusByOwnerAndApprovedFalse() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());
        bookingDto = bookingService.updateStatus(bookingDto.getId(), false, ownerDto.getId());

        assertEquals(Status.REJECTED, bookingDto.getStatus());
    }

    @Test
    void shouldExceptionWhenUpdateStatusByOwnerAndStatusRejected() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());
        bookingDto = bookingService.updateStatus(bookingDto.getId(), false, ownerDto.getId());
        Long bookingId = bookingDto.getId();

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.updateStatus(bookingId, false, ownerDto.getId()));
        assertEquals(String.format("Невозможно изменить статус для бронирования с id = %d!", bookingId),
                exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        UserDto otherUserDto = userService.create(user3);

        Long otherUserId = otherUserDto.getId();
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());

        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> bookingService.get(bookingDto.getId(), otherUserId));
        assertEquals(String.format("У пользователя с id = %d нет прав для просмотра информации о бронировании с id = %d!",
                otherUserId, bookingDto.getId()), exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetAllBookingsInUnknownStateByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusYears(1));
        bookingService.create(bookingInDto1, bookerDto.getId());

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingByUserId(bookerDto.getId(), "UNKNOWN_STATE"));
        assertEquals("Unknown state: UNKNOWN_STATE", exp.getMessage());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingByUserId(bookerDto.getId(), "ALL");
        assertEquals(2, listBookings.size());
    }


    @Test
    void shouldReturnBookingsWhenGetAllBookingsInCurrentStateByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusYears(1));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingByUserId(bookerDto.getId(), "CURRENT");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInPastStateByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusYears(10),
                LocalDateTime.now().minusYears(5));
        BookingDto bookingDto1 = bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusYears(2),
                LocalDateTime.now().minusYears(1));
        BookingDto bookingDto2 = bookingService.create(bookingInDto1, bookerDto.getId());

        bookingService.updateStatus(bookingDto1.getId(), true, ownerDto.getId());
        bookingService.updateStatus(bookingDto2.getId(), true, ownerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingByUserId(bookerDto.getId(), "PAST");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInFutureStateByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusYears(1));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingByUserId(bookerDto.getId(), "FUTURE");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInWaitingStateByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingByUserId(bookerDto.getId(), "WAITING");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnNoBookingsWhenGetAllBookingsInRejectedStateByUserId() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingByUserId(bookerDto.getId(), "REJECTED");
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItems() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems(ownerDto.getId(), "ALL");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateCurrent() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5));
        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().plusMinutes(50));
        BookingDto bookingDto1 = bookingService.create(bookingInDto1, bookerDto.getId());

        bookingService.updateStatus(bookingDto.getId(), true, ownerDto.getId());
        bookingService.updateStatus(bookingDto1.getId(), true, ownerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems(ownerDto.getId(), "CURRENT");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStatePast() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(1));
        BookingDto bookingDto = bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusDays(1));
        BookingDto bookingDto1 = bookingService.create(bookingInDto1, bookerDto.getId());

        bookingService.updateStatus(bookingDto.getId(), true, ownerDto.getId());
        bookingService.updateStatus(bookingDto1.getId(), true, ownerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems(ownerDto.getId(), "PAST");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateFuture() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.now().plusMonths(2),
                LocalDateTime.now().plusMonths(10));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems(ownerDto.getId(), "FUTURE");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateWaiting() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto, bookerDto.getId());

        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems(ownerDto.getId(), "WAITING");
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateRejected() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto, bookerDto.getId());


        BookingInDto bookingInDto1 = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems(ownerDto.getId(), "REJECTED");
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldExceptionWhenItemIsNotAvailableForBooking() {
        UserDto ownerDto = userService.create(user1);
        UserDto bookerDto = userService.create(user2);

        itemDto1.setAvailable(Boolean.FALSE);
        ItemDto itemDto = itemService.create(itemDto1, ownerDto.getId());

        BookingInDto bookingInDto = new BookingInDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        NotAvailableBookingException exp = assertThrows(NotAvailableBookingException.class,
                () -> bookingService.create(bookingInDto, bookerDto.getId()));
        assertEquals(String.format("Вещь с id = %d недоступна для бронирования!", itemDto.getId()), exp.getMessage());
    }

}