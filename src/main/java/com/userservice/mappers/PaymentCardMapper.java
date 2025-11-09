package com.userservice.mappers;

import com.userservice.models.PaymentCard;
import com.userservice.responseDTO.PaymentCardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentCardMapper {

    PaymentCardDTO convertToDTO(PaymentCard paymentCard);

    PaymentCard convertToEntity(PaymentCardDTO paymentCardResponseDTO);


}
