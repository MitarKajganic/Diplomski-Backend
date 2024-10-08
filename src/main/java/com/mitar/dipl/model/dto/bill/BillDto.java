package com.mitar.dipl.model.dto.bill;

import lombok.Data;

@Data
public class BillDto {

    private String id;
    private String orderId;
    private String totalAmount;
    private String tax;
    private String discount;
    private String createdAt;

}
