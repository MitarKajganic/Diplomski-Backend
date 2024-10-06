package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "menus")
@Data
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name; // e.g., Breakfast, Lunch, Dinner

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private Set<MenuItem> items;

    @PrePersist
    public void generateId() {
        if (id == null) id = UUID.randomUUID().toString();
    }
}
