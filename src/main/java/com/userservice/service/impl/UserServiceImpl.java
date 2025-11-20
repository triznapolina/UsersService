package com.userservice.service.impl;


import com.userservice.entity.User;
import com.userservice.entity.dto.UserDto;
import com.userservice.exception.AlreadyExistsException;
import com.userservice.exception.ResourceNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.userservice.specification.UserSpecification.hasFirstName;
import static com.userservice.specification.UserSpecification.hasSurname;


@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDTO) {

        if (userRepository.existsUserByEmail((userDTO.getEmail()))) {
            throw new AlreadyExistsException("User with email=" + userDTO.getEmail() + " is already exists");
        }

        User user = userMapper.convertToEntity(userDTO);
        user.setActive(true);
        user = userRepository.save(user);
        return userMapper.convertToDTO(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDTO, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id="+ id + " is not found"));

        user.setFirstName(userDTO.getFirstName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        user.setBirthDate(userDTO.getBirthDate());
        user = userRepository.save(user);
        return userMapper.convertToDTO(user);
    }


    @Transactional
    @Override
    public void deleteUser(Long id) {
        User choosenUser = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id="+ id + " is not found"));
        userRepository.delete(choosenUser);

    }

    @Transactional
    @Override
    public boolean findByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    @Transactional
    @Override
    public UserDto getUserById(Long id) {
        return userMapper.convertToDTO(userRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("User with id="+ id + " is not found")));
    }


    @Transactional
    @Override
    public void activateDeactivateUser(Long id, boolean active) {
        userRepository.setStatusOfActivity(id, active);
    }

    @Transactional
    @Override
    public Page<User> findUsers(String firstName, String surname, Pageable pageable) {
        Specification<User> spec = hasFirstName(firstName).and(hasSurname(surname));
        return userRepository.findAll(spec, pageable);

    }


    @Transactional
    @Override
    public Page<User> getUsersOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAll(pageable);
    }



}
