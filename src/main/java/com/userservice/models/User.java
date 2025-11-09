package com.userservice.models;

import com.userservice.auditJPA.AuditFieldsEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User extends AuditFieldsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "active")
    private Boolean active;


}