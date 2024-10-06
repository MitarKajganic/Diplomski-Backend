package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "bills")
@Data
public class Bill {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    private double totalAmount;
    private double tax;
    private double discount;
    private double finalAmount;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
