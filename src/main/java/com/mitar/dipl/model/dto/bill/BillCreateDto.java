package com.mitar.dipl.model.dto.bill;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BillCreateDto {

    @NotNull(message = "Order ID cannot be null")
    private String orderId;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotNull(message = "Tax cannot be null")
    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax;

    @NotNull(message = "Discount cannot be null")
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

}
