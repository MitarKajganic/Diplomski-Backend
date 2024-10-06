package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class Order { // Renamed to OrderEntity to avoid conflict with SQL 'Order'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private LocalDateTime orderTime;
    private String status; // e.g., PENDING, COMPLETED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The customer who placed the order

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Bill bill;

    @PrePersist
    public void generateId() {
        if (id == null) id = UUID.randomUUID().toString();
    }
}