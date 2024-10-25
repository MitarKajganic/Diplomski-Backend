package com.mitar.dipl.model.dto.order;

import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import com.mitar.dipl.model.entity.DeliveryInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OrderDto {

    private String id;
    private LocalDateTime createdAt;
    private String status;
    private String userId;
    private Set<OrderItemDto> orderItems;
    private String billId;
    private DeliveryInfo deliveryInfo;

}
