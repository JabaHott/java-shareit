package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    @NotNull
    private LocalDateTime start;
    @Column(name = "end_date")
    @NotNull
    private LocalDateTime end;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booker_id", referencedColumnName = "id", nullable = false)
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public String toString() {
        return "Booking{" +
                "status=" + status +
                ", end=" + end +
                ", start=" + start +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(start, booking.start) && Objects.equals(end, booking.end) && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, status);
    }
}
