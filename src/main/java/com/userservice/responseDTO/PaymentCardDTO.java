package com.userservice.responseDTO;


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

    @NotBlank(message = "Необходимо указать имя держателя")
    private String holder;

    @NotBlank(message = "Необходимо указать номер карты")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String number;

    @NotNull(message = "Необходимо указать дату истечения срока карты")
    private LocalDate expirationDate;
}
