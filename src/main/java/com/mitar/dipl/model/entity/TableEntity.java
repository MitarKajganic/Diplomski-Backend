package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;


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

    private Integer tableNumber;
    private Integer capacity;
    private Boolean isAvailable;

    @OneToMany(mappedBy = "table")
    private Set<Reservation> reservations;

}