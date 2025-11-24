package com.userservice.integtation.service;

import com.userservice.entity.User;
import com.userservice.entity.dto.UserDto;
import com.userservice.exception.ResourceNotFoundException;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootTest
public class UserServiceTest {


    @Autowired
    private UserServiceImpl userService;

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

        userRepository.deleteAll();

    }

    @Test
    void testCreateUser() {

        // Arrange
        UserDto userDto = new UserDto();
        userDto.setFirstName("Ivan");
        userDto.setSurname("Ivanov");
        userDto.setEmail("ivanov@example.com");
        userDto.setBirthDate(LocalDate.of(1990, 5, 20));

        // Act
        UserDto createdUser = userService.createUser(userDto);

        // Assert
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(userDto.getEmail(), createdUser.getEmail());

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
    public void testUpdateUser() {
        // Arrange
        UserDto newUser = new UserDto();
        newUser.setFirstName("John");
        newUser.setSurname("Doe");
        newUser.setEmail("john@example.com");
        newUser.setBirthDate(LocalDate.of(1990, 1, 1));

        UserDto createdUser = userService.createUser(newUser);

        createdUser.setFirstName("Jane");

        // Act
        UserDto updatedUser = userService.updateUser(createdUser, createdUser.getId());

        // Assert
        assertThat(updatedUser.getFirstName()).isEqualTo("Jane");
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        UserDto user = new UserDto();
        user.setFirstName("Temp");
        user.setSurname("User");
        user.setEmail("temp@example.com");
        user.setBirthDate(LocalDate.of(1985, 5, 15));
        UserDto savedUser = userService.createUser(user);

        // Act
        userService.deleteUser(savedUser.getId());

        // Assert
        assertThatThrownBy(() -> userService.getUserById(savedUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }



    @Test
    public void testActivateDeactivateUser() {
        // Arrange
        User testUser = new User();
        testUser.setFirstName("Ivan");
        testUser.setSurname("Ivanov");
        testUser.setEmail("ivanov@example.com");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setActive(true);
        userRepository.save(testUser);

        // Act
        userService.activateDeactivateUser(testUser.getId(), false);

        // Assert
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getActive()).isFalse();
    }

    @Test
    public void testFindUsers() {
        // Arrange
        User testUser = new User();
        testUser.setFirstName("Ivan");
        testUser.setSurname("Ivanov");
        testUser.setEmail("ivanov@example.com");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setActive(true);
        userRepository.save(testUser);

        userRepository.save(new User(null, "Ivan", "Petrov",
                LocalDate.of(1985,5,5),"ivan.petrov@example.com",true));
        userRepository.save(new User(null, "Ivan", "Sidorov",
                LocalDate.of(1992,2,2),"ivan.sidorov@example.com", true));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> result = userService.findUsers("Ivan", "Petrov", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        User user = result.getContent().get(0);
        assertEquals("Ivan", user.getFirstName());
        assertEquals("Petrov", user.getSurname());

    }

    @Test
    public void testGetUsersOnPage() {
        // Arrange
        userRepository.save(new User(null, "Ivan", "Petrov",
                LocalDate.of(1985,5,5),"ivan.petrov@example.com",true));
        userRepository.save(new User(null, "Ivan", "Sidorov",
                LocalDate.of(1992,2,2),"ivan.sidorov@example.com", true));

        // Act
        Page<User> page = userService.getUsersOnPage(0, 2);

        // Assert
        assertThat(page.getContent().size()).isLessThanOrEqualTo(2);
        assertThat(page.getTotalPages()).isGreaterThan(0);
    }



}
