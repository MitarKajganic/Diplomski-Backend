package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;
import com.mitar.dipl.model.entity.Order;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrderMapper {

    private UserRepository userRepository;

    public OrderDto toDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId().toString());
        orderDto.setCreatedAt(order.getCreatedAt().toString());
        orderDto.setStatus(order.getStatus());
        order.setUser(order.getUser());
        order.setOrderItems(order.getOrderItems());
        order.setBill(order.getBill());
        return orderDto;
    }

    public Order toEntity(OrderCreateDto orderCreateDto) {
        Order order = new Order();
        order.setStatus(orderCreateDto.getStatus());
        order.setOrderItems(orderCreateDto.getOrderItems());
        Optional<User> user = userRepository.findById(UUID.fromString(orderCreateDto.getUserId()));
        user.ifPresent(order::setUser);
        return order;
    }
}
