package com.mitar.dipl.model.dto.order_item;

import lombok.Data;

@Data
public class OrderItemCreateDto {

    private String price;
    private String quantity;
    private String orderId;
    private String menuItemId;

}
