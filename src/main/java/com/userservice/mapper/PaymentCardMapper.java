package com.userservice.mapper;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.dto.PaymentCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentCardMapper {

    PaymentCardDto convertToDTO(PaymentCard paymentCard);

    PaymentCard convertToEntity(PaymentCardDto paymentCardResponseDTO);


}
