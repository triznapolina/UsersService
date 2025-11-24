package com.userservice.integtation.service;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDto;
import com.userservice.exception.ResourceNotFoundException;
import com.userservice.repository.PaymentCardRepository;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
public class PaymentCardServiceTest {


    @Autowired
    private PaymentCardServiceImpl paymentCardService;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("usersDB")
            .withUsername("postgres")
            .withPassword("root");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);


    @DynamicPropertySource
    static void registerContainers(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);


        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @AfterEach
    void setAfter(){

        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

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
        // Arrange
        User testUser = new User();
        testUser.setFirstName("Ivan");
        testUser.setSurname("Ivanov");
        testUser.setEmail("ivanov@example.com");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setActive(true);
        userRepository.save(testUser);

        PaymentCardDto dto = new PaymentCardDto();
        dto.setHolder("Test Holder");
        dto.setNumber("1234567890123456");
        dto.setExpirationDate(LocalDate.now().plusYears(3));

        long initialCount = userRepository.countCardsByUserId(testUser.getId());

        // Act
        PaymentCardDto created = paymentCardService.createCard(dto, testUser.getId());

        // Assert
        assertNotNull(created);
        assertEquals(dto.getHolder(), created.getHolder());
        assertTrue(paymentCardRepository.existsById(created.getId()));

        long newCount = userRepository.countCardsByUserId(testUser.getId());
        assertEquals(initialCount + 1, newCount);
    }


    @Test
    void testUpdateCard_Success() {
        // Arrange
        User testUser = new User();
        testUser.setFirstName("Ivan");
        testUser.setSurname("Ivanov");
        testUser.setEmail("ivanov@example.com");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setActive(true);
        userRepository.save(testUser);

        PaymentCardDto dto = new PaymentCardDto();
        dto.setHolder("Initial Holder");
        dto.setNumber("9876543210987654");
        dto.setExpirationDate(LocalDate.now().plusYears(2));

        PaymentCardDto created = paymentCardService.createCard(dto, testUser.getId());

        PaymentCardDto updateDto = new PaymentCardDto();
        updateDto.setHolder("Updated Holder");
        updateDto.setExpirationDate(LocalDate.now().plusYears(4));

        // Act
        PaymentCardDto updated = paymentCardService.updateCard(updateDto, created.getId());

        // Assert
        assertEquals("Updated Holder", updated.getHolder());
        assertEquals(updateDto.getExpirationDate(), updated.getExpirationDate());
    }

    @Test
    void testDeleteCard_Success() {
        // Arrange
        User testUser = new User();
        testUser.setFirstName("Ivan");
        testUser.setSurname("Ivanov");
        testUser.setEmail("ivanov@example.com");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setActive(true);
        userRepository.save(testUser);

        PaymentCardDto dto = new PaymentCardDto();
        dto.setHolder("Holder");
        dto.setNumber("1111222233334444");
        dto.setExpirationDate(LocalDate.now().plusYears(2));

        PaymentCardDto created = paymentCardService.createCard(dto, testUser.getId());

        // Act
        paymentCardService.deleteCard(created.getId());


        // Aseert
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentCardService.findById(created.getId());
        });
    }



}
