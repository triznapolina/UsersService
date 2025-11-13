package com.userservice.integtationTests.repository;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest
public class UserRepositoryTest {


    private final UserRepository userRepository;
    private User testUser;

    @Autowired
    public UserRepositoryTest (UserRepository userRepository) {
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
        User user = new User(1L, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);
        testUser = userRepository.save(user);

        PaymentCard card = new PaymentCard(1L, "PetrovPetr", "5285666699998888",
                LocalDate.of(2025, 4,18), true, testUser);

    }

    @AfterEach
    void setAfter(){

        userRepository.deleteAll();

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
    void testCountCardsByUserId() {
        long count = userRepository.countCardsByUserId(testUser.getId());
        assertEquals(1, count);
    }

    @Test
    void testSetStatusOfActivity() {
        int updatedCount = userRepository.setStatusOfActivity(testUser.getId(), false);
        assertEquals(1, updatedCount);

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertFalse(updatedUser.getActive());
    }



}
