package com.userservice.services.impl;

import com.userservice.mappers.UserMapper;
import com.userservice.models.User;
import com.userservice.models.dto.UserDTO;
import com.userservice.repositories.UserRepository;
import com.userservice.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.userservice.specification.UserSpecification.createSpecification;

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
    @Override
    public void deleteUser(Long id) {
        User choosenUser = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("This user is not found"));
        userRepository.delete(choosenUser);

    }

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
        Specification<User> spec = createSpecification(firstName, surname);
        return userRepository.findAll(spec, pageable);
    }


    @Override
    public Page<User> getUsersOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAll(pageable);
    }



}
