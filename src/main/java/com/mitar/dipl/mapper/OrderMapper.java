package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {

    private UserRepository userRepository;

    private OrderItemMapper orderItemMapper;

    public OrderDto toDto(OrderEntity orderEntity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderEntity.getId().toString());
        orderDto.setCreatedAt(orderEntity.getCreatedAt());
        orderDto.setStatus(orderEntity.getStatus().name());
        orderDto.setOrderItems(orderEntity.getOrderItems().stream().map(orderItemMapper::toDto).collect(Collectors.toSet()));
        orderDto.setUserId(orderEntity.getUser().getId().toString());
        return orderDto;
    }

    public OrderEntity toEntity(OrderCreateDto orderCreateDto) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(Status.valueOf(orderCreateDto.getStatus()));
        orderEntity.setOrderItems(orderCreateDto.getOrderItems());
        Optional<User> user = userRepository.findById(UUID.fromString(orderCreateDto.getUserId()));
        user.ifPresent(orderEntity::setUser);
        return orderEntity;
    }
}
