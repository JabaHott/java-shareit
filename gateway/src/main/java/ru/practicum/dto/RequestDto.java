package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса (description) не должно быть пустым!")
    @Size(max = 512, message = "Описание не должно превышать 512 символов")
    private String description;

    private UserDto requester;

    private LocalDateTime created;

    private List<ItemDto> items;
}