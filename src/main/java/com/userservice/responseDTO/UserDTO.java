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

    @NotBlank(message = "You must specify the name")
    private String firstName;

    @NotBlank(message = "It is necessary to specify the last name")
    private String surname;

    @Email(message = "Incorrectly entered format")
    @NotBlank(message = "The email must not be empty")
    private String email;

    @NotNull(message = "You must specify the date of birth")
    private LocalDate birthDate;
}
