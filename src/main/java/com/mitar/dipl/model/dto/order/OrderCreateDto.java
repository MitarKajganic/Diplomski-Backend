package com.mitar.dipl.model.dto.order;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.HashMap;

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
    private HashMap<String, @Min(value = 1, message = "Quantity must be at least 1 for every given item")Integer> menuItemIdsAndQuantities; // {"id1": 2, "id2": 1}

}
