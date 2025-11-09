package com.userservice.services.impl;

import com.userservice.mappers.PaymentCardMapper;
import com.userservice.models.PaymentCard;
import com.userservice.models.User;
import com.userservice.repositories.PaymentCardRepository;
import com.userservice.repositories.UserRepository;
import com.userservice.responseDTO.PaymentCardDTO;
import com.userservice.services.PaymentCardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    @Autowired
    public PaymentCardRepository paymentCardRepository;

    @Autowired
    public PaymentCardMapper paymentCardMapper;

    @Autowired
    public UserRepository userRepository;


    @Override
    public PaymentCardDTO createCard(PaymentCardDTO paymentCardDTO, long userId) {

        long count = userRepository.countCardsByUserId(userId);
        if (count >= 5) {
            throw new RuntimeException("The user's card limit has been exceeded");
        }

        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("This user is not found"));

        PaymentCard card = paymentCardMapper.convertToEntity(paymentCardDTO);
        card.setUser(user);
        card.setActive(true);
        PaymentCard savedCard = paymentCardRepository.save(card);
        return paymentCardMapper.convertToDTO(savedCard);
    }

    @Transactional
    @Override
    public PaymentCardDTO updateCard(PaymentCardDTO paymentCardDTO, long id) {

       PaymentCard card = paymentCardRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("This card is not found"));

        card.setHolder(paymentCardDTO.getHolder());
        card.setExpirationDate(paymentCardDTO.getExpirationDate());

        PaymentCard updatedCard = paymentCardRepository.save(card);
        return paymentCardMapper.convertToDTO(updatedCard);

    }

    @Transactional
    @Override
    public void deleteCard(long id) {
        paymentCardRepository.deleteById(id);
    }

    @Override
    public PaymentCardDTO findById(Long id) {
        return paymentCardMapper.convertToDTO(paymentCardRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("This card is not found")));
    }

    @Transactional
    @Override
    public void activateDeactivatePaymentCard(Long Id, boolean active) {
        paymentCardRepository.setStatusOfActivity(Id, active);
    }


    @Override
    public List<PaymentCard> getCardsByUserId(Long userId) {
       return paymentCardRepository.findAllCardsByUserId(userId);

    }

    @Override
    public PaymentCard getCardByNumber(String number) {
        return paymentCardRepository.findByNumber(number);
    }

    @Override
    public PaymentCard getCardByHolder(String holder) {
        return paymentCardRepository.findByHolder(holder);
    }

    @Override
    public Page<PaymentCard> getCardsAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return paymentCardRepository.viewAllCards(pageable);
    }

}


