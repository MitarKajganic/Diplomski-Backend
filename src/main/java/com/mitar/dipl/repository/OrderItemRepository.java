package com.mitar.dipl.repository;

import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findAllByMenuItem_Id(UUID menuItemId);

    Optional<OrderItem> findByOrderEntityAndMenuItem(OrderEntity order, MenuItem menuItem);
}
