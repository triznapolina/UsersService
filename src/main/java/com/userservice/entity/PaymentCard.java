package com.userservice.entity;

import com.userservice.auditJPA.AuditFieldsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCard extends AuditFieldsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "holder")
    private String holder;

    @Column(name = "number")
    private String number;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "active")
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



}