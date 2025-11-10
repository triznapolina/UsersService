package com.userservice.controllers;


import com.userservice.exceptions.AlreadyExistsException;
import com.userservice.exceptions.ResourceNotFoundException;
import com.userservice.models.PaymentCard;
import com.userservice.models.User;
import com.userservice.responseDTO.PaymentCardDTO;
import com.userservice.responseDTO.UserDTO;
import com.userservice.services.PaymentCardService;
import com.userservice.services.UserService;
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

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentCardService paymentCardService;


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
    public ResponseEntity<Page<UserDTO>> filterUsers(@RequestParam String surname, Pageable pageable) {
        try {
            Page<UserDTO> page = userService.filterUsersByFirstNameAndSurname(surname, pageable);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
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

    @GetMapping("/users/{userId}/cards")
    public ResponseEntity<List<PaymentCard>> getCardsByUserId(@PathVariable Long userId) {
        List<PaymentCard> cards = paymentCardService.getCardsByUserId(userId);
        if (cards != null) {
            return ResponseEntity.ok(cards);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cards/number")
    public ResponseEntity<PaymentCard> getCardByNumber(@RequestParam String number) {
        PaymentCard card = paymentCardService.getCardByNumber(number);
        if (card != null) {
            return ResponseEntity.ok(card);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cards/holder")
    public ResponseEntity<PaymentCard> getCardByHolder(@RequestParam String holder) {
        PaymentCard card = paymentCardService.getCardByHolder(holder);
        if (card != null) {
            return ResponseEntity.ok(card);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<PaymentCard>> getCardsAll(@RequestParam int pageNo, @RequestParam int pageSize) {
        Page<PaymentCard> page = paymentCardService.getCardsAll(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }


}
