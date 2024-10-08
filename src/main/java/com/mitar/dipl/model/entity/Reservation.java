package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private int numberOfGuests;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // The customer who made the reservation (optional)

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableEntity table;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "guest_phone")
    private String guestPhone;

}
