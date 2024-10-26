package com.mitar.dipl.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@SQLDelete(sql = "UPDATE reservations SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@ToString(exclude = {"table", "user"})
@EqualsAndHashCode(exclude = {"table", "user"})
public class Reservation {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "reservation_time", updatable = false, nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private int numberOfGuests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    @JsonBackReference
    private TableEntity table;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "guest_phone")
    private String guestPhone;

    @Column(nullable = false)
    private Boolean deleted = false;

}
