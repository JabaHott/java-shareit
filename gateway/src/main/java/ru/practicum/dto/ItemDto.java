package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название (name) вещи не должно быть пустым!")
    @Size(max = 255, message = "Имя не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Описание (description) вещи не должно быть пустым!")
    @Size(max = 512, message = "Описание не должно превышать 512 символов")
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
