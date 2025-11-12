package com.userservice.controller;

import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDTO;
import com.userservice.entity.dto.UserDTO;
import com.userservice.exception.AlreadyExistsException;
import com.userservice.exception.ResourceNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.service.PaymentCardService;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/users")
public class UserController {

    private final UserService userService;
    private final PaymentCardService paymentCardService;
    private final UserMapper userMapper;


    @Autowired
    public UserController (UserService userService, PaymentCardService paymentCardService,UserMapper userMapper) {
        this.userService = userService;
        this.paymentCardService = paymentCardService;
        this.userMapper = userMapper;
    }


    @PostMapping("/new")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {

        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable Long id) {
        try {
            UserDTO updatedUser = userService.updateUser(userDTO, id);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateDeactivateUser(@PathVariable Long id, @RequestParam boolean active) {
        try {
            userService.activateDeactivateUser(id, active);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<User>> filterUsers(@RequestParam String firstName, @RequestParam String surname,
                                                  Pageable pageable) {
        try {
            Page<User> page = userService.findUsers(firstName, surname, pageable);
            return ResponseEntity.ok(page);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping
    public ResponseEntity<Page<User>> getUsersOnPage(@RequestParam int pageNo, @RequestParam int pageSize) {
        try {
            Page<User> page = userService.getUsersOnPage(pageNo, pageSize);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/cards/user/{userId}")
    public ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO paymentCardDTO,
                                                     @PathVariable long userId) {
        try {
            PaymentCardDTO createdCard = paymentCardService.createCard(paymentCardDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping("/cards/{id}")
    public ResponseEntity<PaymentCardDTO> updateCard(@Valid @RequestBody PaymentCardDTO paymentCardDTO,
                                                     @PathVariable Long id) {
        try {
            PaymentCardDTO updatedCard = paymentCardService.updateCard(paymentCardDTO, id);
            return ResponseEntity.ok(updatedCard);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable long id) {
        try {
            paymentCardService.deleteCard(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<PaymentCardDTO> getCardById(@PathVariable Long id) {
        try {
            PaymentCardDTO card = paymentCardService.findById(id);
            return ResponseEntity.ok(card);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/cards/{id}/activate")
    public ResponseEntity<Void> activateDeactivatePaymentCard(@PathVariable Long id, @RequestParam boolean active) {
        try {
            paymentCardService.activateDeactivatePaymentCard(id, active);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCard>> getCardsByUser(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            User entityUser = userMapper.convertToEntity(user);
            List<PaymentCard> cards = paymentCardService.findAllByUser(entityUser);
            return ResponseEntity.ok(cards);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/cards")
    public ResponseEntity<Page<PaymentCard>> getCardsAll(@RequestParam int pageNo, @RequestParam int pageSize) {
        Page<PaymentCard> page = paymentCardService.getCardsOnPage(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/cards/search")
    public ResponseEntity<PaymentCard>  searchCard(@RequestParam String holder, @RequestParam String number) {
        PaymentCard card = paymentCardService.findByHolderOrNumber(holder, number);
        if (card != null) {
            return ResponseEntity.ok(card);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



}
