package com.mitar.dipl.model.dto.order;

import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Set;

@Data
public class OrderCreateDto {

    private String status; // e.g., PENDING, COMPLETED, CANCELLED

    private String userId;

    private Set<OrderItem> orderItems;

}
