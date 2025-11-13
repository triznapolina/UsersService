package com.userservice.unitTests.service.impl;

import com.userservice.mapper.PaymentCardMapper;
import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDTO;
import com.userservice.repository.PaymentCardRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardServiceImpl;


    @Test
    void createCardTest() {

        long userId = 2L;

        PaymentCard card = new PaymentCard();
        PaymentCardDTO paymentCardDTO = new PaymentCardDTO();

        User user = new User(userId, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.countCardsByUserId(userId)).thenReturn(2L);
        when(paymentCardMapper.convertToEntity(paymentCardDTO)).thenReturn(card);
        when(paymentCardMapper.convertToDTO(card)).thenReturn(paymentCardDTO);
        when(paymentCardRepository.save(card)).thenReturn(card);

        PaymentCardDTO createdCard = paymentCardServiceImpl.createCard(paymentCardDTO, userId);

        assertThat(createdCard).isNotNull();
        assertEquals(paymentCardDTO, createdCard);

        verify(paymentCardRepository, times(1)).save(card);
    }

    @Test
    public void updateCardTest() {
        Long id = 1L;
        PaymentCardDTO dto = new PaymentCardDTO();
        dto.setHolder("Ivanov");
        dto.setExpirationDate(LocalDate.of(2025, 11, 12));

        PaymentCard existingCard = new PaymentCard();
        existingCard.setId(id);
        existingCard.setHolder("Petrov");
        existingCard.setExpirationDate(LocalDate.of(2024, 11, 11));

        PaymentCard updatedEntity = new PaymentCard();
        updatedEntity.setId(id);
        updatedEntity.setHolder(dto.getHolder());
        updatedEntity.setExpirationDate(dto.getExpirationDate());


        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(existingCard));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(updatedEntity);
        when(paymentCardMapper.convertToDTO(updatedEntity)).thenReturn(new PaymentCardDTO());


        PaymentCardDTO result = paymentCardServiceImpl.updateCard(dto, id);

        assertNotNull(result);

        verify(paymentCardRepository, times(1)).findById(id);
        verify(paymentCardRepository, times(1)).save(existingCard);
    }


    @Test
    public void deleteCardTest() {

        Long id = 1L;
        PaymentCard card = new PaymentCard();
        card.setId(id);

        paymentCardServiceImpl.deleteCard(id);

        verify(paymentCardRepository, times(1)).deleteById(id);
    }


    @Test
    public void findByIdTest() {
        Long id = 1L;
        PaymentCard card = new PaymentCard();
        PaymentCardDTO dto = new PaymentCardDTO();

        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(card));
        when(paymentCardMapper.convertToDTO(card)).thenReturn(dto);

        PaymentCardDTO result = paymentCardServiceImpl.findById(id);

        assertNotNull(result);

        verify(paymentCardRepository, times(1)).findById(id);
    }


    @Test
    public void activateDeactivatePaymentCardTest() {
        Long id = 1L;
        boolean active = false;

        paymentCardServiceImpl.activateDeactivatePaymentCard(id, active);

        verify(paymentCardRepository, times(1)).setStatusOfActivity(id, active);
    }


    @Test
    public void findAllByUserTest() {
        User user = new User();
        List<PaymentCard> cards = Arrays.asList(new PaymentCard(), new PaymentCard());

        when(paymentCardRepository.findAllByUser(user)).thenReturn(cards);

        List<PaymentCard> result = paymentCardServiceImpl.findAllByUser(user);

        assertEquals(2, result.size());


        verify(paymentCardRepository, times(1)).findAllByUser(user);
    }


    @Test
    public void findByHolderOrNumberTest() {
        String holder = "Polina Trizna";
        String number = "1111 5555 0022 6258";
        PaymentCard card = new PaymentCard();

        when(paymentCardRepository.findByHolderOrNumber(holder, number)).thenReturn(card);

        PaymentCard result = paymentCardServiceImpl.findByHolderOrNumber(holder, number);

        assertNotNull(result);

        verify(paymentCardRepository, times(1)).findByHolderOrNumber(holder, number);
    }


    @Test
    public void getCardsOnPageTest() {
        int pageNo = 0;
        int pageSize = 10;
        List<PaymentCard> cards = Arrays.asList(new PaymentCard(), new PaymentCard());
        Page<PaymentCard> page = new PageImpl<>(cards);

        when(paymentCardRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<PaymentCard> result = paymentCardServiceImpl.getCardsOnPage(pageNo, pageSize);

        assertEquals(2, result.getContent().size());

        verify(paymentCardRepository, times(1)).findAll(any(Pageable.class));
    }


}
