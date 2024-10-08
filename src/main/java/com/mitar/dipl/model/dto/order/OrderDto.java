package com.mitar.dipl.model.dto.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.model.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class OrderDto {

    private String id;

    private String createdAt;

    private String status; // e.g., PENDING, COMPLETED, CANCELLED

    private String user; // The customer who placed the order

    private Set<OrderItemDto> orderItems;

    private Bill bill;
}
