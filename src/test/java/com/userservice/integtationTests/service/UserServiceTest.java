package com.userservice.integtationTests.service;

import com.userservice.entity.User;
import com.userservice.entity.dto.UserDTO;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootApplication
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private UserDTO createdUser;


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


    @BeforeEach
    void setBefore() {

        createdUser = userService.createUser(new UserDTO(1L, "Polina",
                "Trizna","poliaTr@gmail.com", LocalDate.of(2006, 4, 29)));



    }

    @AfterEach
    void setAfter(){

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
    void testCreateUser() {


        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
    }


    @Test
    void testGetUserById() {

        UserDTO retrieved = userService.getUserById(createdUser.getId());

        assertNotNull(retrieved);
    }


    @Test
    void testUpdateUser() {

        UserDTO updateDto = new UserDTO(1L, "Polina",
                "Trizna","polinatrizna1@gmail.com", LocalDate.of(2006, 4, 29));

        UserDTO updated = userService.updateUser(updateDto, createdUser.getId());

        assertEquals("Polina", updated.getFirstName());
        assertEquals("polina@gmail.com", updated.getEmail());
    }


    @Test
    void testDeleteUser() {

        assertNotNull(userService.getUserById(createdUser.getId()));

        userService.deleteUser(createdUser.getId());

        assertNull(userService.getUserById(createdUser.getId()));


    }

    @Test
    void testActivateDeactivateUser() {

        Long userId = createdUser.getId();
        userService.activateDeactivateUser(userId, false);
        User user = userRepository.findById(userId).orElseThrow();


        assertThat(user.getActive()).isFalse();


        userService.activateDeactivateUser(userId, true);
        user = userRepository.findById(userId).orElseThrow();


        assertThat(user.getActive()).isTrue();



    }

    @Test
    void testFindUsersByFirstNameAndSurname() {

        List<User> listUser = getUsers();
        userRepository.saveAll(listUser);

        Page<User> result = userService.findUsers("Polina", "Trizna",
                PageRequest.of(0, 10));


        assertEquals(1, result.getContent().size());
        User user = result.getContent().get(0);
        assertEquals("Polina", user.getFirstName());
        assertEquals("Trizna", user.getSurname());
    }

    private static List<User> getUsers() {
        User user1 = new User(1L, "Polina", "Trizna",
                LocalDate.of(2006, 4, 11), "polia@gmail.com", true);
        User user2 = new User(2L, "Kirill", "Kirillov",
                LocalDate.of(2003, 10, 17), "kirillov@gmail.com", false);
        User user3 = new User(3L, "Petr", "Petrov",
                LocalDate.of(1994, 2, 25), "petrov@gmail.com", true);

        List<User> listUser = new ArrayList<>();
        listUser.add(user1);
        listUser.add(user2);
        listUser.add(user3);
        return listUser;
    }


    @Test
    void testGetUsersOnPage() {

        for (int i=1; i <= 20; i++) {
            userRepository.save(new User());
        }

        Page<User> page = userService.getUsersOnPage(1, 10);

        assertEquals(10, page.getContent());
    }


}
