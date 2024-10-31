package com.mitar.dipl.model.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {

    private String id;
    private LocalDateTime transactionTime;
    private BigDecimal amount;
    private String type;
    private String method;
    private String billId;
    private String stripeUrl;
    private String stripeSessionId;
    private String stripeStatus;

}
