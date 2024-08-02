package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название (name) вещи не должно быть пустым!")
    private String name;
    @NotBlank(message = "Описание (description) вещи не должно быть пустым!")
    private String description;
    @NotNull(message = "Не указан статус (available) вещи!")
    private Boolean available;
    private Long requestId;
    private BookingWithBookerIdDto lastBooking;
    private BookingWithBookerIdDto nextBooking;
    private List<CommentDto> comments;

    public ItemDto(Long id, String name, String description, Boolean available, List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comments = comments;
    }
}
