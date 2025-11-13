package com.userservice.integtationTests.repository;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.repository.PaymentCardRepository;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
public class PaymentCardRepositoryTest {


    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    private User testUser;
    private PaymentCard testCard;

    @Autowired
    public PaymentCardRepositoryTest(PaymentCardRepository paymentCardRepository,
                                     UserRepository userRepository){
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
    }


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




    @BeforeEach
    void setUp() {

        testUser = new User(1L, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);
        testUser = userRepository.save(testUser);

        PaymentCard card = new PaymentCard(1L, "TriznaPolina", "0125457899665487",
                LocalDate.of(2025, 4,18), true, testUser);

        testCard = paymentCardRepository.save(card);
    }

    @AfterEach
    void setAfter(){

        paymentCardRepository.deleteAll();

    }

    @BeforeAll
    static void setUpAll() {
        postgres.start();
    }

    @AfterAll
    static void setDownAll() {
        postgres.stop();
    }


    @Test
    void testFindAllByUser() {

        List<PaymentCard> cards = paymentCardRepository.findAllByUser(testUser);

        assertFalse(cards.isEmpty());
        assertTrue(cards.contains(testCard));
    }

    @Test
    void testFindByHolderOrNumber() {
        PaymentCard foundByHolder =
                paymentCardRepository.findByHolderOrNumber("TriznaPolina", "0125457899665487");
        assertNotNull(foundByHolder);
        assertEquals(testCard.getNumber(), foundByHolder.getNumber());
    }

    @Test
    void testSetStatusOfActivity() {
        int updated = paymentCardRepository.setStatusOfActivity(testCard.getId(), false);
        assertEquals(1, updated);

        PaymentCard updatedCard = paymentCardRepository.findById(testCard.getId()).orElseThrow();
        assertFalse(updatedCard.getActive());
    }





}
