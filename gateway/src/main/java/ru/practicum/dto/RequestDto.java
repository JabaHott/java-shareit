package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.controllers.BaseControllerInterface;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestDto {

    @NotBlank(message = "Описание запроса (description) не должно быть пустым!", groups = BaseControllerInterface.Create.class)
    @Size(max = 512, message = "Описание не должно превышать 512 символов", groups = {BaseControllerInterface.Create.class, BaseControllerInterface.Update.class})
    private String description;

    private List<ItemDto> items;
}