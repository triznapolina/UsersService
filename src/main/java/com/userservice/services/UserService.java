package com.userservice.services;

import com.userservice.models.User;
import com.userservice.models.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO, Long id);

    void deleteUser(Long id);


    UserDTO getUserById(Long id);

    void activateDeactivateUser(Long id, boolean active);

    Page<User> findUsers(String firstName, String surname, Pageable pageable);

    Page<User> getUsersOnPage(int pageNo, int pageSize);

}
