package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.dto.bill.BillDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.service.implementation.UUIDUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class BillMapper {

    private OrderRepository orderRepository;

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
