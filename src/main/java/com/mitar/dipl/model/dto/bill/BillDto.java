package com.mitar.dipl.model.dto.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillDto {

    private String id;
    private String orderId;
    private BigDecimal totalAmount;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;

}
