package com.userservice.mapper;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.dto.PaymentCardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentCardMapper {

    PaymentCardDTO convertToDTO(PaymentCard paymentCard);

    PaymentCard convertToEntity(PaymentCardDTO paymentCardResponseDTO);


}
