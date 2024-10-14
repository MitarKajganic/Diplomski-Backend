package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @CreationTimestamp
    @Column(name = "transaction_time", updatable = false, nullable = false)
    private LocalDateTime transactionTime;
    private BigDecimal amount;
    private String type; // e.g., PAYMENT, REFUND

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

}
