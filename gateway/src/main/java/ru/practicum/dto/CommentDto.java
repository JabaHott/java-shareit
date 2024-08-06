package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.controllers.BaseControllerInterface;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @NotBlank(groups = BaseControllerInterface.Create.class)
    @Size(max = 65535, message = "Имя не должно превышать 255 символов", groups = {BaseControllerInterface.Create.class, BaseControllerInterface.Update.class})
    private String text;

}