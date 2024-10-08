package com.mitar.dipl.model.dto.order_item;

import lombok.Data;

@Data
public class OrderItemDto {

    private String id;
    private String price;
    private String quantity;
    private String orderId;
    private String menuItemId;

}
