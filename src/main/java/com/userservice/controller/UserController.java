package com.userservice.controller;

import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.UserDto;
import com.userservice.entity.dto.UserInfoResponse;
import com.userservice.exception.AlreadyExistsException;
import com.userservice.service.PaymentCardService;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.userservice.controller.PaymentCardController.getUserInfoResponse;

@RestController
@RequestMapping("/app/users")
public class UserController {

    private final UserService userService;
    private final PaymentCardService paymentCardService;

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public UserController (UserService userService, PaymentCardService paymentCardService, RestTemplate restTemplate) {
        this.userService = userService;
        this.paymentCardService = paymentCardService;
        this.restTemplate = restTemplate;
    }


    @PostMapping("/new")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDTO,
                                              @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (userService.findByEmail(userDTO.getEmail())) {
            throw new AlreadyExistsException("User with email=" + userDTO.getEmail() + " is already exists");
        }
        UserDto createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDTO,
                                              @PathVariable Long id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (userService.findByEmail(userDTO.getEmail())) {
            throw new AlreadyExistsException("User with email=" + userDTO.getEmail() + " is already exists");
        }
        UserDto updatedUser = userService.updateUser(userDTO, id);
        return ResponseEntity.ok(updatedUser);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateDeactivateUser(@PathVariable Long id,
                                                       @RequestParam boolean active,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.activateDeactivateUser(id, active);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<User>> filterUsers(@RequestParam String firstName,
                                                  @RequestParam String surname,
                                                  Pageable pageable,
                                                  @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Page<User> page = userService.findUsers(firstName, surname, pageable);
        return ResponseEntity.ok(page);
    }


    @GetMapping
    public ResponseEntity<Page<User>> getUsersOnPage(@RequestParam int pageNo, @RequestParam int pageSize,
                                                     @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Page<User> page = userService.getUsersOnPage(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCard>> getCardsByUser(@PathVariable Long userId,
                                                            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = userService.getUserById(userId);
        List<PaymentCard> cards = paymentCardService.findAllByUser(user);
        return ResponseEntity.ok(cards);

    }


    private UserInfoResponse getUserInfoFromAuthService(String token) {
        return getUserInfoResponse(token, authServiceUrl, restTemplate);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization header is missing or invalid");
    }

}