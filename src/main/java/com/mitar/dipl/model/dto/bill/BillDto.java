package com.mitar.dipl.model.dto.bill;

import com.mitar.dipl.model.entity.OrderEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillDto {

    private String id;
    private BigDecimal totalAmount;
    private BigDecimal tax;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private String orderId;

}
