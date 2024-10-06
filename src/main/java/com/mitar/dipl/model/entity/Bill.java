package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bills")
@Data
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalAmount;
    private double tax;
    private double discount;
    private double finalAmount;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
