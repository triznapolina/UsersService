package com.userservice.repositories;

import com.userservice.models.PaymentCard;
import com.userservice.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    List<PaymentCard> findAllByUser(User user);

    Page<PaymentCard> findAll(Pageable pageable);

    PaymentCard findByHolderOrNumber(String holder, String number);

    @Modifying
    @Query("update PaymentCard c set c.active = :active where c.id = :cardId")
    int setStatusOfActivity(@Param("cardId") Long cardId, @Param("active") boolean active);


}
