package com.mitar.dipl.model.dto.bill;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BillCreateDto {

    @NotNull(message = "Order ID cannot be null")
    @NotEmpty(message = "Order ID cannot be empty")
    private String orderId;

}
