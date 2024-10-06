package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ingredientName;
    private int quantity;
    private String unit; // e.g., kg, liters

    private boolean lowStock; // Flag for low stock alerts

}