package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.dto.bill.BillDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
public class BillMapper {

    private OrderRepository orderRepository;

    public BillDto toDto(Bill bill) {
        BillDto billDto = new BillDto();
        billDto.setId(bill.getId().toString());
        billDto.setOrderId(bill.getOrder().getId().toString());
        billDto.setTotalAmount(bill.getTotalAmount().toString());
        billDto.setTax(bill.getTax().toString());
        billDto.setDiscount(bill.getDiscount().toString());
        billDto.setCreatedAt(bill.getCreatedAt().toString());
        return billDto;
    }

    public Bill toEntity(BillCreateDto billCreateDto) {
        Bill bill = new Bill();
        bill.setTotalAmount(billCreateDto.getTotalAmount());
        bill.setTax(billCreateDto.getTax());
        bill.setDiscount(billCreateDto.getDiscount());
        bill.setOrder(orderRepository.findById(UUID.fromString(billCreateDto.getOrderId())).get());
        return bill;
    }
}
