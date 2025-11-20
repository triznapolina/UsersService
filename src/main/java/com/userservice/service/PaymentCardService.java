package com.userservice.service;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentCardService {



    PaymentCardDto createCard(PaymentCardDto paymentCardDTO, long userId);

    PaymentCardDto updateCard(PaymentCardDto paymentCardDTO, long id);

    void deleteCard(long id);

    PaymentCardDto findById(Long id);


    void activateDeactivatePaymentCard(Long Id, boolean active);

    List<PaymentCard> findAllByUser(User user);

    PaymentCard findByHolderOrNumber(String holder, String number);

    Page<PaymentCard> getCardsOnPage(int pageNo, int pageSize);

    boolean findByNumber( String number);

}
