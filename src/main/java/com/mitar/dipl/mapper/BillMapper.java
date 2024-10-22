package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.bill.BillDto;
import com.mitar.dipl.model.entity.Bill;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BillMapper {

    public BillDto toDto(Bill bill) {
        BillDto billDto = new BillDto();

        billDto.setId(bill.getId().toString());
        billDto.setTotalAmount(bill.getTotalAmount());
        billDto.setTax(bill.getTax());
        billDto.setFinalAmount(bill.getFinalAmount());
        billDto.setCreatedAt(bill.getCreatedAt());
        billDto.setOrderId(bill.getOrderEntity().getId().toString());

        return billDto;
    }

}
