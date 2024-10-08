package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.dto.bill.BillDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.Order;
import com.mitar.dipl.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
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
        Optional<Order> order = orderRepository.findById(UUID.fromString(billCreateDto.getOrderId()));
        if (order.isPresent()) {
            bill.setOrder(order.get());
            order.ifPresent(o -> o.setBill(bill));
            orderRepository.save(order.get());
        }
        return bill;
    }
}