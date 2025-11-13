package com.userservice.integtationTests.controller;


import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDTO;
import com.userservice.entity.dto.UserDTO;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootApplication
@Testcontainers
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    private RestTemplate restTemplate;


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


    @Test
    void createUserTest() {
        UserDTO userDto = new UserDTO(1L, "Polina",
                "Trizna","poliaTr@gmail.com", LocalDate.of(2006, 4, 29));

        ResponseEntity<UserDTO> response = restTemplate.postForEntity("/new", userDto, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    @Test
    void getUserByIdTest() {
        Long userId = 1L;

        ResponseEntity<UserDTO> response = restTemplate.getForEntity("/" + userId, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



    @Test
    void updateUserTest() {
        Long userId = 1L;
        UserDTO userDto = new UserDTO(userId, "Polina",
                "Trizna","poliaTr7@gmail.com", LocalDate.of(2006, 4, 29));

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<UserDTO> entity = new HttpEntity<>(userDto, headers);
        ResponseEntity<UserDTO> response = restTemplate.exchange("/" + userId, HttpMethod.PUT, entity,
                UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getEmail()).isEqualTo("poliaTr7@gmail.com");
    }


    @Test
    void deleteUserTest() {
        Long userId = 2L;
        User user = new User(userId, "Kirill", "Kirillov",
                LocalDate.of(1885, 10, 11), "kirillov@gmail.com", true);
        userRepository.save(user);

        ResponseEntity<Void> response = restTemplate.exchange("/" + userId, HttpMethod.DELETE, null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void activateDeactivateUserTest() {
        Long userId = 1L;
        boolean active = false;

        ResponseEntity<Void> response = restTemplate.exchange("/users/" + userId + "/activate?active=" + active,
                HttpMethod.PUT, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void filterUsersTest() {
        ResponseEntity<Page<User>> response = restTemplate.exchange(
                "/users/filter?firstName=Polina&surname=Trizna&page=0&size=1",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Page<User>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    void getUsersOnPageTest() {
        ResponseEntity<Page<User>> response = restTemplate.exchange("/users?pageNo=0&pageSize=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Page<User>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createCardTest() {
        PaymentCardDTO cardDto = new PaymentCardDTO(1L, "PetrovPetr", "5285666699998888",
                LocalDate.of(2030, 4,25));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentCardDTO> entity = new HttpEntity<>(cardDto, headers);

        ResponseEntity<PaymentCardDTO> response = restTemplate.postForEntity(
                "/users/cards/user/1",
                entity,
                PaymentCardDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateCardTest() {
        PaymentCardDTO dto = new PaymentCardDTO(1L, "KirillovKirill", "5285666699998888",
                LocalDate.of(2030, 4,25));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentCardDTO> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<PaymentCardDTO> response = restTemplate.exchange(
                "/cards/1",
                HttpMethod.PUT,
                entity,
                PaymentCardDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getHolder()).isEqualTo("KirillovKirill");
    }


    @Test
    void getCardsByUserTest() {

        ResponseEntity<List<PaymentCard>> response = restTemplate.exchange(
                "/user/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PaymentCard>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    void getCardsAllTest() {
        ResponseEntity<Page<PaymentCard>> response = restTemplate.exchange(
                "/cards?pageNo=0&pageSize=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Page<PaymentCard>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void searchCardTest() {
        ResponseEntity<PaymentCard> response = restTemplate.getForEntity(
                "/cards/search?holder=KirillovKirill&number=5285666699998888",
                PaymentCard.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }




}
