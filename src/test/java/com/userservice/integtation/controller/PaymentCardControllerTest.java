package com.userservice.integtation.controller;

import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDto;
import com.userservice.repository.PaymentCardRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.PaymentCardService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentCardControllerTest {

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardService paymentCardService;

    @Autowired
    private TestRestTemplate restTemplate;


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("usersDB")
            .withUsername("postgres")
            .withPassword("root");


    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }


    @BeforeAll
    static void setUp() {
        postgres.start();
    }

    @AfterAll
    static void setDown() {
        postgres.stop();
    }

    @AfterEach
    void setAfter(){
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    void createCardTest() {

        // Arrange
        User user = new User(null, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);
        userRepository.save(user);

        Long createdUserId = user.getId();

        PaymentCardDto cardDto = new PaymentCardDto(null, "Kirillov", "5285666699998888",
                LocalDate.of(2030, 4, 25));

        // Act
        ResponseEntity<String> cardResponse = restTemplate.postForEntity(
                "/app/cards/user/" + createdUserId,
                new HttpEntity<>(cardDto),
                String.class
        );

        // Assert
        assertThat(cardResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateCardTest() {

        // Arrange
        User user = new User(null, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);

        userRepository.save(user);

        PaymentCard card = new PaymentCard();
        card.setHolder("InitialHolder");
        card.setNumber("1234567890123456");
        card.setExpirationDate(LocalDate.of(2025, 12, 31));
        card.setActive(true);
        card.setUser(user);

        paymentCardRepository.save(card);
        Long cardId = card.getId();

        PaymentCard updatedCard = new PaymentCard();
        updatedCard.setId(cardId);
        updatedCard.setHolder("Lakir");
        updatedCard.setNumber("5285666699998888");
        updatedCard.setExpirationDate(LocalDate.of(1885, 10, 11));
        updatedCard.setUser(user);

        // Act
        ResponseEntity<PaymentCard> response = restTemplate.exchange(
                "/app/cards/" + cardId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedCard),
                PaymentCard.class
        );


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getHolder()).isEqualTo("Lakir");
    }

    @Test
    void searchCardTest() {

        // Arrange
        User user = new User(null, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);

        userRepository.save(user);
        Long userId = user.getId();

        PaymentCardDto card = new PaymentCardDto(null, "Kirillov", "5285666699998888",
                LocalDate.of(2030, 4, 25));

        paymentCardService.createCard(card, userId);

        // Act
        ResponseEntity<PaymentCard> response = restTemplate.getForEntity(
                "/app/cards/search?holder=KirillovKirill&number=5285666699998888",
                PaymentCard.class
        );


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }




}
