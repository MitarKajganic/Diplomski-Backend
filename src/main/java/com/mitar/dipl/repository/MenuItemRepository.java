package com.mitar.dipl.repository;

import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    List<MenuItem> findAllByMenu(Menu menu);

    List<MenuItem> findAllByCategory(String category);

    Optional<MenuItem> findByName(String name);

    List<MenuItem> findAllByNameContainingIgnoreCase(String name);
}
