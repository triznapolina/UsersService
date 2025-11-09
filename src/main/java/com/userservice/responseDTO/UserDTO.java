package com.userservice.responseDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotBlank(message = "Необходимо указать имя")
    private String firstName;

    @NotBlank(message = "Необходимо указать фамилию")
    private String surname;

    @Email(message = "Неверно введенный формат")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

    @NotNull(message = "Необходимо указать дату рождения")
    private LocalDate birthDate;
}
