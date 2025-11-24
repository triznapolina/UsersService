package com.userservice.unit.service.impl;

import com.userservice.entity.dto.UserDto;
import com.userservice.exception.ResourceNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.entity.User;
import com.userservice.repository.UserRepository;
import com.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void createUserTest(){
        // Arrange
        User user = new User();
        UserDto userDTO = new UserDto();
        when(userMapper.convertToEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToDTO(user)).thenReturn(userDTO);

        // Act
        UserDto createdUser = userService.createUser(userDTO);

        // Assert
        assertThat(createdUser).isNotNull();
        assertEquals(userDTO, createdUser);

    }



    @Test
    public void updateUserTest() {

        //Arrange
        Long id = 1L;

        User existingUser = new User(id, "Polina", "Trizna",
                LocalDate.of(2006, 4, 29),"polia@gmail.com", true);

        UserDto dto = new UserDto(id, "Polina", "Trizna","poliaTr@gmail.com",
                LocalDate.of(2006, 4, 29));

        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setFirstName(dto.getFirstName());
        updatedUser.setSurname(dto.getSurname());
        updatedUser.setEmail(dto.getEmail());
        updatedUser.setBirthDate(dto.getBirthDate());

        UserDto resultUpdatedDTO = new UserDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.convertToDTO(updatedUser)).thenReturn(resultUpdatedDTO);

        // Act
        UserDto result = userService.updateUser(dto, id);


        // Assert
        assertThat(result).isNotNull();
        assertEquals(resultUpdatedDTO, result);
    }


    @Test
    public void deleteUserTest() {

        //Arrange
        Long id = 1L;
        User user = new User();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(id);

        // Assert
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(id);
        });
    }

    @Test
    void getUserByIdTest() {

        // Arrange
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        User retrievedUser = userService.getUserById(id);

        // Assert
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(id);
    }


    @Test
    void activateDeactivateUserTest(){
        // Arrange
        Long userId = 1L;
        boolean newStatus = false;

        when(userRepository.setStatusOfActivity(userId, newStatus)).thenReturn(1);

        // Act
        userService.activateDeactivateUser(userId, newStatus);

        //Assert
        assertDoesNotThrow(() -> {
            userService.activateDeactivateUser(userId, newStatus);
        });


    }

    @Test
    void findUsersTest() {
        // Arrange
        String firstName = "Polina";
        String surname = "Trizna";
        Pageable pageable = PageRequest.of(0, 1);

        List<User> users = Arrays.asList(new User(), new User(), new User());
        users.get(0).setFirstName(firstName);
        users.get(0).setSurname(surname);
        users.get(1).setFirstName("Petr");
        users.get(1).setSurname("Retrov");
        users.get(2).setFirstName("Ivan");
        users.get(2).setSurname("Ivanov");

        Page<User> pageResult = new PageImpl<>(users, pageable, users.size());
        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

        // Act
        Page<User> result = userService.findUsers(firstName, surname, pageable);

        // Assert
        assertFalse(result.getContent().isEmpty());

        User foundUser = result.getContent().get(0);
        assertEquals(firstName, foundUser.getFirstName());
        assertEquals(surname, foundUser.getSurname());
    }

    @Test
    void getUsersOnPageTest(){

        // Arrange
        int pageNo = 2;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<User> result = userService.getUsersOnPage(pageNo, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());


    }


}
