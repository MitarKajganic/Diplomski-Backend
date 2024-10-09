package com.mitar.dipl.model.dto.order_item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    private String id;
    private BigDecimal price;
    private Integer quantity;
    private String orderId;
    private String menuItemId;

}
