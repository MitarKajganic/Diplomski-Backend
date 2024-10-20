package com.mitar.dipl.model.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionCreateDto {

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be greater than or equal to zero")
    private BigDecimal amount;

    @Pattern(regexp = "PAYMENT|REFUND", message = "Type must be either PAYMENT or REFUND")
    private String type;

    @NotNull(message = "Bill ID cannot be null")
    @NotEmpty(message = "Bill ID cannot be empty")
    private String billId;

    @NotNull(message = "Method cannot be null")
    @Pattern(regexp = "CASH|CARD", message = "Method must be either CASH or CARD")
    private String method;
}
