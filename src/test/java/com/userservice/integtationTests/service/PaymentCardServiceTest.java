package com.userservice.integtationTests.service;

import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDTO;
import com.userservice.mapper.PaymentCardMapper;
import com.userservice.repository.PaymentCardRepository;
import com.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootApplication
@Testcontainers
public class PaymentCardServiceTest {

    @Autowired
    private PaymentCardServiceImpl paymentCardService;

    @Autowired
    private PaymentCardMapper paymentCardMapper;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private User testUser;
    private PaymentCard paymentCard;
    private PaymentCardDTO createdCard;


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("usersDB")
            .withUsername("postgres")
            .withPassword("root");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);


    @DynamicPropertySource
    static void registerContainers(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);


        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }


    @BeforeEach
    void setBefore() {

        testUser = new User(1L, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);

        paymentCard = new PaymentCard(1L, "PetrovPetr", "5285666699998888",
                LocalDate.of(2025, 4,18), true, testUser);


        paymentCard.setUser(testUser);

        PaymentCardDTO converted = paymentCardMapper.convertToDTO(paymentCard);

        createdCard = paymentCardService.createCard(converted,testUser.getId());


    }

    @AfterEach
    void setAfter(){

        paymentCardRepository.deleteAll();

    }

    @BeforeAll
    static void setUp() {
        postgres.start();
        redis.start();
    }

    @AfterAll
    static void setDown() {
        postgres.stop();
        redis.stop();
    }



    @Test
    void testCreateCard() {

        assertThat(createdCard).isNotNull();

        assertThat(paymentCard.getHolder()).isEqualTo(createdCard.getHolder());
        assertThat(paymentCard.getNumber()).isEqualTo(createdCard.getNumber());

    }

    @Test
    void testUpdateCard() {

        PaymentCardDTO updateDto = new PaymentCardDTO(1L, "PetrovPetr", "5285666699998888",
                LocalDate.of(2030, 4,25));
        PaymentCardDTO updated = paymentCardService.updateCard(updateDto, paymentCard.getId());

        assertThat(updated.getExpirationDate()).isEqualTo(LocalDate.of(2030, 4, 25));
    }

    @Test
    void testDeleteCard() {

        assertNotNull(paymentCardService.findById(paymentCard.getId()));

        paymentCardService.deleteCard(paymentCard.getId());

        assertNull(paymentCardService.findById(paymentCard.getId()));
    }

    @Test
    void testFindById() {

        PaymentCardDTO found = paymentCardService.findById(paymentCard.getId());

        assertEquals(found.getId(), paymentCard.getId());
    }

    @Test
    void testActivateDeactivatePaymentCard() {

        paymentCardService.activateDeactivatePaymentCard(paymentCard.getId(), false);
        PaymentCard card = paymentCardRepository.findById(paymentCard.getId()).orElseThrow();

        assertThat(card.getActive()).isFalse();


        paymentCardService.activateDeactivatePaymentCard(paymentCard.getId(), true);
        card = paymentCardRepository.findById(paymentCard.getId()).orElseThrow();

        assertThat(card.getActive()).isTrue();
    }

    @Test
    void testFindAllByUser() {

        List<PaymentCard> cards = paymentCardService.findAllByUser(testUser);
        assertEquals(1, cards.size());
    }



}
