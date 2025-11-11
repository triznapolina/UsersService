package com.userservice.models.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Data
@Setter
@Getter
public class PaymentCardDTO {

    private Long id;

    @NotBlank(message = "The name of the holder must be specified")
    private String holder;

    @NotBlank(message = "You must specify the card number")
    @Pattern(regexp = "\\d{16}", message = "The card number must contain 16 digits")
    private String number;

    @NotNull(message = "You must specify the expiration date of the card")
    private LocalDate expirationDate;
}
