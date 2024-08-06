package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.controllers.BaseControllerInterface;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemDto {

    @NotBlank(message = "Название (name) вещи не должно быть пустым!", groups = BaseControllerInterface.Create.class)
    @Size(max = 255, message = "Имя не должно превышать 255 символов", groups = {BaseControllerInterface.Create.class, BaseControllerInterface.Update.class})
    private String name;

    @NotBlank(message = "Описание (description) вещи не должно быть пустым!", groups = BaseControllerInterface.Create.class)
    @Size(max = 512, message = "Описание не должно превышать 512 символов", groups = {BaseControllerInterface.Create.class, BaseControllerInterface.Update.class})
    private String description;

    @NotNull(message = "Не указан статус (available) вещи!")
    private Boolean available;

    private Long requestId;

}
