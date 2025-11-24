package com.userservice.controller;

import com.userservice.entity.PaymentCard;
import com.userservice.entity.dto.PaymentCardDto;
import com.userservice.entity.dto.UserInfoResponse;
import com.userservice.exception.AlreadyExistsException;
import com.userservice.service.PaymentCardService;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/app/cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public PaymentCardController (PaymentCardService paymentCardService, RestTemplate restTemplate) {
        this.paymentCardService = paymentCardService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PaymentCardDto> createCard(@Valid @RequestBody PaymentCardDto paymentCardDTO,
                                                     @PathVariable long userId,
                                                     @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        if (paymentCardService.findByNumber(paymentCardDTO.getNumber())) {
            throw new AlreadyExistsException("Card with number=" + paymentCardDTO.getNumber() + " is already exists");
        }

        PaymentCardDto createdCard = paymentCardService.createCard(paymentCardDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDto> updateCard(@Valid @RequestBody PaymentCardDto paymentCardDTO,
                                                     @PathVariable Long id,
                                                     @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (paymentCardService.findByNumber(paymentCardDTO.getNumber())) {
            throw new AlreadyExistsException("Card with number=" + paymentCardDTO.getNumber() + " is already exists");
        }

        PaymentCardDto updatedCard = paymentCardService.updateCard(paymentCardDTO, id);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable long id,
                                           @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        paymentCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCard> getCardById(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        PaymentCard card = paymentCardService.findById(id);
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateDeactivatePaymentCard(@PathVariable Long id,
                                                              @RequestParam boolean active,
                                                              @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        paymentCardService.activateDeactivatePaymentCard(id, active);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PaymentCard>> getCardsAll(@RequestParam int pageNo,
                                                         @RequestParam int pageSize,
                                                         @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Page<PaymentCard> page = paymentCardService.getCardsOnPage(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    public ResponseEntity<PaymentCard> searchCard(@RequestParam String holder,
                                                  @RequestParam String number,
                                                  @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        UserInfoResponse userInfo = getUserInfoFromAuthService(token);

        if (!userInfo.getRole().contains("USER")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        PaymentCard card = paymentCardService.findByHolderOrNumber(holder, number);
        return ResponseEntity.ok(card);
    }

    private UserInfoResponse getUserInfoFromAuthService(String token) {
        return getUserInfoResponse(token, authServiceUrl, restTemplate);
    }

    @Nullable
    static UserInfoResponse getUserInfoResponse(String token, String authServiceUrl, RestTemplate restTemplate) {
        String url = authServiceUrl + "/auth/user-info?token=" + token;
        try {
            ResponseEntity<UserInfoResponse> response = restTemplate.getForEntity(url, UserInfoResponse.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error getting user info from Auth Service: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling Auth Service: " + e.getMessage());
        }
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization header is missing or invalid");
    }


}
