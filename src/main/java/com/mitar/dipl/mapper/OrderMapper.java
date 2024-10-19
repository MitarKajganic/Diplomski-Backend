package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderDto toDto(OrderEntity orderEntity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderEntity.getId().toString());
        orderDto.setCreatedAt(orderEntity.getCreatedAt());
        orderDto.setStatus(orderEntity.getStatus().name());
        orderDto.setOrderItems(orderEntity.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet()));
        orderDto.setUserId(orderEntity.getUser().getId().toString());

        // Map billId if Bill is present
        Bill bill = orderEntity.getBill();
        if (bill != null) {
            orderDto.setBillId(bill.getId().toString());
        } else {
            orderDto.setBillId(null);
        }

        return orderDto;
    }

}
