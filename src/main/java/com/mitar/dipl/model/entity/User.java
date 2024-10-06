package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Ensure passwords are hashed

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, STAFF, CUSTOMER

    @OneToMany(mappedBy = "user")
    private Set<Reservation> reservations;

    @PrePersist
    public void generateId() {
        if (id == null) id = UUID.randomUUID().toString();
    }
}
