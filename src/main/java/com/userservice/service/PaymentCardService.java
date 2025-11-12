package com.userservice.service;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentCardService {


    PaymentCardDTO createCard(PaymentCardDTO paymentCardDTO, long userId);

    PaymentCardDTO updateCard(PaymentCardDTO paymentCardDTO, long id);

    void deleteCard(long id);

    PaymentCardDTO findById(Long id);


    void activateDeactivatePaymentCard(Long Id, boolean active);

    List<PaymentCard> findAllByUser(User user);

    PaymentCard findByHolderOrNumber(String holder, String number);

    Page<PaymentCard> getCardsOnPage(int pageNo, int pageSize);
}
