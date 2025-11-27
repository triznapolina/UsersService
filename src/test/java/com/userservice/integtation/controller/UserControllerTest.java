package com.userservice.integtation.controller;


import com.userservice.entity.User;
import com.userservice.entity.dto.UserDto;
import com.userservice.repository.UserRepository;
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
public class UserControllerTest {


    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();

    }


    @Test
    void createUserTest() {

        // Arrange
        UserDto userDto = new UserDto(null, "Polina", "Trizna", "poliaTr@gmail.com",
                LocalDate.of(2006, 4, 29));

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/app/users/new", userDto, String.class);


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    @Test
    void getUserByIdTest() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/app/users/" + userId, UserDto.class);


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    @Test
    void updateUserTest() {

        // Arrange
        User user = new User(null, "Polina", "Trizna",
                LocalDate.of(1885, 10, 11), "trizna@gmail.com", true);
        userRepository.save(user);

        Long userId = user.getId();

        UserDto userDto = new UserDto();
        userDto.setEmail("triznaMa@gmail.com");
        userDto.setFirstName("Polya");
        userDto.setSurname("Trizna");
        userDto.setBirthDate(LocalDate.of(1885, 10, 11));

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/app/users/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(userDto),
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }


    @Test
    void deleteUserTest() {

        // Arrange
        User user = new User(null, "Polina", "Trizna",
                LocalDate.of(1885, 10, 11), "trizna@gmail.com", true);
        userRepository.save(user);

        Long createdUserId = user.getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/app/users/" + createdUserId,
                HttpMethod.DELETE,
                null,
                Void.class
        );


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        boolean userExists = userRepository.existsById(createdUserId);
        assertThat(userExists).isTrue();
    }

    @Test
    void activateDeactivateUserTest() {

        // Arrange
        boolean active = false;
        User user = new User(null, "Polina", "Trizna",
                LocalDate.of(1885, 10, 11), "trizna@gmail.com", true);

        userRepository.save(user);
        Long createdUserId = user.getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/app/users/" + createdUserId + "/activate?active=" + active,
                HttpMethod.PUT,
                null,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }



}
