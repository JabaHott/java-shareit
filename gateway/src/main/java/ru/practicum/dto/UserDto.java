package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.controllers.BaseControllerInterface;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = BaseControllerInterface.Create.class)
    @Size(max = 255, message = "Имя не должно превышать 255 символов")
    private String name;
    @NotEmpty(groups = BaseControllerInterface.Create.class)
    @Email(groups = {BaseControllerInterface.Create.class, BaseControllerInterface.Update.class})
    @Size(max = 512, message = "Почта не должна превышать 512 символов")
    private String email;
}
