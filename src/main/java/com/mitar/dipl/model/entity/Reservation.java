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

    private LocalDateTime reservationTime;
    private int numberOfGuests;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The customer who made the reservation

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableEntity table;

}
