package com.userservice.unit.service.impl;

import com.userservice.exception.ResourceNotFoundException;
import com.userservice.mapper.PaymentCardMapper;
import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDto;
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
        // Arrange
        long userId = 2L;
        PaymentCard card = new PaymentCard();
        PaymentCardDto paymentCardDTO = new PaymentCardDto();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.countCardsByUserId(userId)).thenReturn(2L);
        when(paymentCardMapper.convertToEntity(paymentCardDTO)).thenReturn(card);
        when(paymentCardMapper.convertToDTO(card)).thenReturn(paymentCardDTO);
        when(paymentCardRepository.save(card)).thenReturn(card);

        // Act
        PaymentCardDto createdCard = paymentCardServiceImpl.createCard(paymentCardDTO, userId);

        // Assert
        assertThat(createdCard).isNotNull();
        assertEquals(paymentCardDTO, createdCard);
    }

    @Test
    public void updateCardTest() {
        // Arrange
        Long id = 1L;
        PaymentCardDto dto = new PaymentCardDto();
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

        PaymentCardDto dtoResult = new PaymentCardDto();
        dtoResult.setHolder(dto.getHolder());
        dtoResult.setExpirationDate(dto.getExpirationDate());

        when(paymentCardMapper.convertToDTO(updatedEntity)).thenReturn(dtoResult);

        // Act
        PaymentCardDto result = paymentCardServiceImpl.updateCard(dto, id);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getHolder(), result.getHolder());
        assertEquals(dto.getExpirationDate(), result.getExpirationDate());
    }

    @Test
    public void deleteCardTest() {
        // Arrange
        Long id = 1L;
        PaymentCard card = new PaymentCard();
        card.setId(id);
        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(card));

        // Act
        paymentCardServiceImpl.deleteCard(id);

        // Assert
        when(paymentCardRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentCardServiceImpl.findById(id);
        });
    }

    @Test
    public void findByIdTest() {
        // Arrange
        Long id = 1L;
        PaymentCard card = new PaymentCard();
        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(card));

        // Act
        PaymentCard result = paymentCardServiceImpl.findById(id);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void setStatusOfActivityTest() {
        // Arrange
        Long cardId = 2L;
        boolean newStatus = false;

        // Act
        paymentCardServiceImpl.activateDeactivatePaymentCard(cardId, newStatus);


        //Assert
        assertDoesNotThrow(() -> {
            paymentCardServiceImpl.activateDeactivatePaymentCard(cardId, newStatus);
        });
    }

    @Test
    public void findAllByUserTest() {
        //Arrange
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<PaymentCard> cards = Arrays.asList(new PaymentCard(), new PaymentCard());
        when(paymentCardRepository.findAllByUser(user)).thenReturn(cards);

        //Act
        List<PaymentCard> result = paymentCardServiceImpl.findAllByUser(user);

        //Assert
        assertEquals(2, result.size());
    }

    @Test
    public void findByHolderOrNumberTest() {
        // Arrange
        String holder = "Polina Trizna";
        String number = "1111 5555 0022 6258";
        PaymentCard card = new PaymentCard();
        card.setHolder(holder);
        card.setNumber(number);
        when(paymentCardRepository.findByHolderOrNumber(holder, number)).thenReturn(card);

        // Act
        PaymentCard result = paymentCardServiceImpl.findByHolderOrNumber(holder, number);

        // Assert
        assertNotNull(result);
        assertEquals(holder, result.getHolder());
        assertEquals(number, result.getNumber());
    }

    @Test
    public void getCardsOnPageTest() {
        // Arrange
        int pageNo = 0;
        int pageSize = 10;
        List<PaymentCard> cards = Arrays.asList(new PaymentCard(), new PaymentCard());
        Page<PaymentCard> page = new PageImpl<>(cards);

        when(paymentCardRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<PaymentCard> result = paymentCardServiceImpl.getCardsOnPage(pageNo, pageSize);

        // Assert
        assertEquals(2, result.getContent().size());
    }

}
