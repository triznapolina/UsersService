package com.userservice.services;

import com.userservice.models.PaymentCard;
import com.userservice.responseDTO.PaymentCardDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentCardService {


    PaymentCardDTO createCard(PaymentCardDTO paymentCardDTO, long userId);

    PaymentCardDTO updateCard(PaymentCardDTO paymentCardDTO, long id);

    void deleteCard(long id);

    PaymentCardDTO findById(Long id);


    void activateDeactivatePaymentCard(Long Id, boolean active);


    List<PaymentCard> getCardsByUserId(Long userId);


    PaymentCard getCardByNumber(String number);

    PaymentCard getCardByHolder(String holder);

    Page<PaymentCard> getCardsAll(int pageNo, int pageSize);
}
