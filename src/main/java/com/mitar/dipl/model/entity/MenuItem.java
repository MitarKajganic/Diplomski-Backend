package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "menu_items")
@Data
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String category; // e.g., Appetizer, Main Course, Dessert

    // Relationships
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

}
