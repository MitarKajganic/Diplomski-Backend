package com.mitar.dipl.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "menus")
@Getter
@Setter
public class Menu {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "menu", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<MenuItem> items = new HashSet<>();

    public void addMenuItem(MenuItem menuItem) {
        items.add(menuItem);
        menuItem.setMenu(this);
    }

    public void removeMenuItem(MenuItem menuItem) {
        items.remove(menuItem);
        menuItem.setMenu(null);
    }
}
