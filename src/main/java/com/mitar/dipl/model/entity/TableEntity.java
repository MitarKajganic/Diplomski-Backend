package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tables")
@Data
public class TableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean isAvailable;

    @OneToMany(mappedBy = "table")
    private Set<Reservation> reservations = new HashSet<>();

}