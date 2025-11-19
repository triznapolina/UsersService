package com.userservice.entity.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

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
