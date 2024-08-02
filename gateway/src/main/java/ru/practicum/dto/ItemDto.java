package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.dto.BookingWithBookerIdDto;
import ru.practicum.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название (name) вещи не должно быть пустым!")
    private String name;

    @NotBlank(message = "Описание (description) вещи не должно быть пустым!")
    private String description;

    @NotNull(message = "Не указан статус (available) вещи!")
    private Boolean available;

    @JsonIgnore
    private UserDto owner;

    private Long requestId;

    private BookingWithBookerIdDto lastBooking;

    private BookingWithBookerIdDto nextBooking;

    private List<CommentDto> comments;
}
