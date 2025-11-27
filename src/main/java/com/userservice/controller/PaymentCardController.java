package com.userservice.controller;

import com.userservice.entity.PaymentCard;
import com.userservice.entity.User;
import com.userservice.entity.dto.PaymentCardDto;
import com.userservice.exception.AlreadyExistsException;
import com.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController (PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PaymentCardDto> createCard(@Valid @RequestBody PaymentCardDto paymentCardDTO,
                                                     @PathVariable long userId) {

        if (paymentCardService.findByNumber(paymentCardDTO.getNumber())) {
            throw new AlreadyExistsException("Card with number=" + paymentCardDTO.getNumber() + " is already exists");
        }

        PaymentCardDto createdCard = paymentCardService.createCard(paymentCardDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDto> updateCard(@Valid @RequestBody PaymentCardDto paymentCardDTO,
                                                     @PathVariable Long id) {
        PaymentCardDto updatedCard = paymentCardService.updateCard(paymentCardDTO, id);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable long id) {
        paymentCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCard> getCardById(@PathVariable Long id) {

        PaymentCard card = paymentCardService.findById(id);
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<PaymentCard> activateDeactivatePaymentCard(@PathVariable Long id,
                                                              @RequestParam boolean active) {
        PaymentCard updatedCard = paymentCardService.activateDeactivatePaymentCard(id, active);
        return ResponseEntity.ok(updatedCard);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PaymentCard>> getCardsAll(@RequestParam int pageNo,
                                                         @RequestParam int pageSize) {
        Page<PaymentCard> page = paymentCardService.getCardsOnPage(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    public ResponseEntity<PaymentCard> searchCard(@RequestParam String holder,
                                                  @RequestParam String number) {
        PaymentCard card = paymentCardService.findByHolderOrNumber(holder, number);
        return ResponseEntity.ok(card);
    }

}
