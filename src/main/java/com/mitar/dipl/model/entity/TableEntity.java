package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.util.Set;

@Entity
@Table(name = "tables")
@Data
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tableNumber;
    private int capacity;
    private boolean isAvailable;

    @OneToMany(mappedBy = "table")
    private Set<Reservation> reservations;

}