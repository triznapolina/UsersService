package com.userservice.services.impl;

import com.userservice.mappers.PaymentCardMapper;
import com.userservice.models.PaymentCard;
import com.userservice.models.User;
import com.userservice.models.dto.PaymentCardDTO;
import com.userservice.models.dto.UserDTO;
import com.userservice.repositories.PaymentCardRepository;
import com.userservice.repositories.UserRepository;
import com.userservice.services.PaymentCardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {


    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final UserRepository userRepository;


    @Autowired
    public PaymentCardServiceImpl (PaymentCardRepository paymentCardRepository,
                                   PaymentCardMapper paymentCardMapper,
                                   UserRepository userRepository) {
        this.paymentCardRepository = paymentCardRepository;
        this.paymentCardMapper = paymentCardMapper;
        this.userRepository = userRepository;
    }



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
    @CachePut(
            value = "card",
            key = "#id"
    )
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
    @CacheEvict(
            value = "card",
            key = "#id"
    )
    @Override
    public void deleteCard(long id) {
        paymentCardRepository.deleteById(id);
    }


    @Cacheable(
            value = "card",
            key = "#id"
    )
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


    @Cacheable(
            value = "card",
            key = "#user.id"
    )
    @Override
    public List<PaymentCard> findAllByUser(User user) {
        return paymentCardRepository.findAllByUser(user);
    }


    @Override
    public PaymentCard findByHolderOrNumber(String holder, String number) {
        return paymentCardRepository.findByHolderOrNumber(holder, number);
    }

    @Override
    public Page<PaymentCard> getCardsOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return paymentCardRepository.findAll(pageable);
    }

}
