package com.mitar.dipl.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tables")
@Getter
@Setter
@ToString(exclude = "reservations")
@EqualsAndHashCode(exclude = "reservations")
public class TableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean isAvailable;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Reservation> reservations = new HashSet<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setTable(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setTable(null);
    }

}