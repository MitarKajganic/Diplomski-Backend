package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String password; // Ensure passwords are hashed

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role; // ADMIN, STAFF, CUSTOMER

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "user")
    private Set<Reservation> reservations;

    public void setHashPassword(String plainTextPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(9);
        this.password = bCryptPasswordEncoder.encode(plainTextPassword);
    }

}
