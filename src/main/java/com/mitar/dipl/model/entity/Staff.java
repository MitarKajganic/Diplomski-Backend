package com.mitar.dipl.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "staff")
@Data
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position;
    private String contactInfo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Staff user account

}
