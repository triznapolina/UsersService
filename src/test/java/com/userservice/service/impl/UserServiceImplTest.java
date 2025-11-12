package com.userservice.service.impl;

import com.userservice.mapper.UserMapper;
import com.userservice.entity.User;
import com.userservice.entity.dto.UserDTO;
import com.userservice.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        User user = new User(1L,"Polina", "Trizna",
                LocalDate.of(2006, 4, 29),"polinatr@gmail.com", true);

        UserDTO userDTO = new UserDTO(1L,"Polina", "Trizna",
                "polinatr@gmail.com", LocalDate.of(2006, 4, 29));

        when(userMapper.convertToEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToDTO(user)).thenReturn(userDTO);

        UserDTO createdUser = userService.createUser(userDTO);

        assertThat(createdUser).isNotNull();
        assertEquals(userDTO, createdUser);

        verify(userRepository, times(1)).save(user);

    }



    @Test
    public void updateUserTest() {
        Long id = 1L;

        User existingUser = new User(id, "Polina", "Trizna",
                LocalDate.of(2006, 4, 29),"polia@gmail.com", true);

        UserDTO dto = new UserDTO(id, "Polina", "Trizna","poliaTr@gmail.com",
                LocalDate.of(2006, 4, 29));

        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setFirstName(dto.getFirstName());
        updatedUser.setSurname(dto.getSurname());
        updatedUser.setEmail(dto.getEmail());
        updatedUser.setBirthDate(dto.getBirthDate());

        UserDTO resultUpdatedDTO = new UserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.convertToDTO(updatedUser)).thenReturn(resultUpdatedDTO);

        UserDTO result = userService.updateUser(dto, id);

        assertThat(result).isNotNull();
        assertEquals(resultUpdatedDTO, result);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(existingUser);
    }


    @Test
    public void deleteUserTest() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void getUserByIdTest() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        UserDTO userDto = new UserDTO();
        userDto.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.convertToDTO(user)).thenReturn(userDto);

        UserDTO retrievedUser = userService.getUserById(id);

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(id);

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void activateDeactivateUserTest(){
        Long id = 1L;
        boolean active = false;

        userService.activateDeactivateUser(id, active);

        verify(userRepository, times(1)).setStatusOfActivity(id, active);


    }

    @Test
    void findUsersTest(){

        String firstName = "Polina";
        String surname = "Trizna";
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(new User(), new User());
        Page<User> pageResult = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

        Page<User> result = userService.findUsers(firstName, surname, pageable);

        assertThat(result).isNotNull();

        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));

    }

    @Test
    void getUsersOnPageTest(){

        int pageNo = 2;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> page = new PageImpl<>(List.of(new User()));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getUsersOnPage(pageNo, pageSize);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);


    }


}
