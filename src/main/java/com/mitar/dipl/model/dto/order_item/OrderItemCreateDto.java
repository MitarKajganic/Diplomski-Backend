package com.mitar.dipl.model.dto.order_item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemCreateDto {

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be greater than or equal to zero")
    private BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Order ID cannot be null")
    @NotEmpty(message = "Order ID cannot be empty")
    private String orderId;

    @NotNull(message = "Menu item ID cannot be null")
    @NotEmpty(message = "Menu item ID cannot be empty")
    private String menuItemId;

}
