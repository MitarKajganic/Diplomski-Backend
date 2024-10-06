package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Data
public class MenuItem {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;
    private String description;
    private double price;
    private String category; // e.g., Appetizer, Main Course, Dessert

    // Relationships
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

}
