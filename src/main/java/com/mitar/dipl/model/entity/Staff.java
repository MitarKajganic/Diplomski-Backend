package com.mitar.dipl.model.entity;

import com.mitar.dipl.model.entity.enums.Position;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "staff")
@Data
public class Staff extends User {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;

    @Column(nullable = false)
    private String contactInfo;

}
