package com.userservice.service;

import com.userservice.entity.User;
import com.userservice.entity.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserDto createUser(UserDto userDTO);

    UserDto updateUser(UserDto userDTO, Long id);

    void deleteUser(Long id);


    UserDto getUserById(Long id);

    void activateDeactivateUser(Long id, boolean active);

    Page<User> findUsers(String firstName, String surname, Pageable pageable);

    Page<User> getUsersOnPage(int pageNo, int pageSize);

}
