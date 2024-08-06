package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Transactional(readOnly = true)
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Transactional(readOnly = true)
    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    @Transactional(readOnly = true)
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Transactional(readOnly = true)
    List<Booking> findByBookerIdAndStatusAndEndIsBefore(Long bookerId, Status status, LocalDateTime end, Sort sort);

    @Transactional(readOnly = true)
    List<Booking> findByBookerIdAndStatusInAndStartIsAfter(Long bookerId, List<Status> statuses, LocalDateTime start, Sort sort);

    @Transactional(readOnly = true)
    List<Booking> findAllBookingsByItemId(Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN (" +
            "      SELECT i.id FROM Item i" +
            "      WHERE i.owner.id = ?1) " +
            "ORDER BY b.start desc")
    @Transactional(readOnly = true)
    List<Booking> findAllBookingsByOwner(Long ownerIds);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN (" +
            "      SELECT i.id FROM Item i" +
            "      WHERE i.owner.id = ?1) " +
            "  AND b.status IN ?2 " +
            " ORDER BY b.start desc")
    @Transactional(readOnly = true)
    List<Booking> findAllBookingsByOwner(Long ownerId, List<Status> statuses);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "  AND b.item.id = ?2 " +
            "  AND b.status = 'APPROVED' " +
            "  AND b.start < ?3")
    @Transactional(readOnly = true)
    List<Booking> findPastAndCurrentActiveBookingsByBookerIdAndItemId(Long userId, Long itemId, LocalDateTime dateTime);
}
