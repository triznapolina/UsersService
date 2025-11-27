package com.userservice.service.impl;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDto;
import com.userservice.exception.AlreadyExistsException;
import com.userservice.exception.ResourceNotFoundException;
import com.userservice.mapper.PaymentCardMapper;
import com.userservice.repository.PaymentCardRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.PaymentCardService;
import jakarta.transaction.Transactional;
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


    public PaymentCardServiceImpl (PaymentCardRepository paymentCardRepository,
                                   PaymentCardMapper paymentCardMapper,
                                   UserRepository userRepository) {
        this.paymentCardRepository = paymentCardRepository;
        this.paymentCardMapper = paymentCardMapper;
        this.userRepository = userRepository;
    }



    @Transactional
    @Override
    public PaymentCardDto createCard(PaymentCardDto paymentCardDTO, long userId) {

        long count = userRepository.countCardsByUserId(userId);
        if (count >= 5) {
            throw new AlreadyExistsException("The user's card limit has been exceeded");
        }

        User user = userRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException("User with id="+ userId + " is not found"));

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
    public PaymentCardDto updateCard(PaymentCardDto paymentCardDTO, long id) {

        PaymentCard card = paymentCardRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Card with id="+ id + " is not found"));

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

    @Transactional
    @Cacheable(
            value = "card",
            key = "#id"
    )
    @Override
    public PaymentCard findById(Long id) {
        return paymentCardRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Card with id="+ id + " is not found"));
    }

    @Transactional
    @CachePut(
            value = "user",
            key = "#id"
    )
    @Override
    public PaymentCard activateDeactivatePaymentCard(Long id, boolean active) {
        paymentCardRepository.setStatusOfActivity(id, active);
        return paymentCardRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Card not found with id: " + id));
    }

    @Transactional
    @Cacheable(
            value = "card",
            key = "#user.id"
    )
    @Override
    public List<PaymentCard> findAllByUser(User user) {

        userRepository.findById(user.getId()).orElseThrow(()
                -> new ResourceNotFoundException("User with id="+ user.getId() + " is not found"));

        return paymentCardRepository.findAllByUser(user);
    }


    @Transactional
    @Override
    public PaymentCard findByHolderOrNumber(String holder, String number) {
        return paymentCardRepository.findByHolderOrNumber(holder, number);
    }

    @Transactional
    @Override
    public Page<PaymentCard> getCardsOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return paymentCardRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public boolean findByNumber(String number) {
        return paymentCardRepository.existsPaymentCardByNumber(number);
    }

}