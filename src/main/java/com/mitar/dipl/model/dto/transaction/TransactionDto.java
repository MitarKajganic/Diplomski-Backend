package com.mitar.dipl.model.dto.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {

    private String id;
    private LocalDateTime transactionTime;
    private BigDecimal amount;
    private String type;
    private String billId;

}
