package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime reservationTime;
    private int numberOfGuests;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The customer who made the reservation

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableEntity table;

}
