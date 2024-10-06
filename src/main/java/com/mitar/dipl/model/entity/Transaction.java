package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime transactionTime;
    private double amount;
    private String type; // e.g., PAYMENT, REFUND

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

}
