package com.mitar.dipl.model.dto.order;

import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OrderDto {

    private String id;
    private LocalDateTime createdAt;
    private String status; // e.g., PENDING, COMPLETED, CANCELLE
    private String userId; // The customer who placed the order
    private Set<OrderItemDto> orderItems;
    private String billId;

}
