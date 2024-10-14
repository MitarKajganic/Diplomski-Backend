package com.mitar.dipl.model.dto.order;

import com.mitar.dipl.model.entity.OrderItem;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class OrderCreateDto {

    @NotNull(message = "Status cannot be null")
    @NotEmpty(message = "Status cannot be empty")
    @Pattern(regexp = "PENDING|COMPLETED|CANCELLED", message = "Status must be either PENDING, COMPLETED, or CANCELLED")
    private String status; // e.g., PENDING, COMPLETED, CANCELLED

    @NotNull(message = "User ID cannot be null")
    @NotEmpty(message = "User ID cannot be empty")
    private String userId;

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "Items must contain at least one item")
    @Size(min = 1, message = "Items must contain at least one item")
    private Set<OrderItem> orderItems;

}
