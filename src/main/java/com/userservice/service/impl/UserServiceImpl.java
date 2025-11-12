package com.userservice.service.impl;


import com.userservice.entity.User;
import com.userservice.entity.dto.UserDTO;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);
        user.setActive(true);
        user = userRepository.save(user);
        return userMapper.convertToDTO(user);
    }

    @Transactional
    @CachePut(
            value = "user",
            key = "#id"
    )
    @Override
    public UserDTO updateUser(UserDTO userDTO, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This user is not found"));
        user.setFirstName(userDTO.getFirstName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        user.setBirthDate(userDTO.getBirthDate());
        user = userRepository.save(user);
        return userMapper.convertToDTO(user);
    }


    @Transactional
    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Override
    public void deleteUser(Long id) {
        User choosenUser = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("This user is not found"));
        userRepository.delete(choosenUser);

    }

    @Cacheable(
            value = "user",
            key = "#id"
    )
    @Override
    public UserDTO getUserById(Long id) {
        return userMapper.convertToDTO(userRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("This user is not found")));
    }


    @Transactional
    @Override
    public void activateDeactivateUser(Long id, boolean active) {
        userRepository.setStatusOfActivity(id, active);
    }

    @Override
    public Page<User> findUsers(String firstName, String surname, Pageable pageable) {
        Specification<User> spec = hasFirstName(firstName).and(hasSurname(surname));
        return userRepository.findAll(spec, pageable);

    }


    @Override
    public Page<User> getUsersOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAll(pageable);
    }



}
