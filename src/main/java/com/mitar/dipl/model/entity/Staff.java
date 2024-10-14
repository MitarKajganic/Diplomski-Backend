package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "staff")
@Data
public class Staff {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;
    private String surname;
    private String position;
    private String contactInfo;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
