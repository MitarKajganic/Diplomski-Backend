package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class OrderItemMapper {

    private OrderRepository orderRepository;

    private MenuItemRepository menuItemRepository;

    public OrderItemDto toDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId().toString());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setQuantity(orderItem.getQuantity());
        orderItemDto.setOrderId(orderItem.getOrderEntity().getId().toString());
        orderItemDto.setMenuItemId(orderItem.getMenuItem().getId().toString());
        return orderItemDto;
    }

    public OrderItem toEntity(OrderItemCreateDto orderItemCreateDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(orderItemCreateDto.getPrice());
        orderItem.setQuantity(orderItemCreateDto.getQuantity());
        OrderEntity orderEntity = orderRepository.findById(UUID.fromString(orderItemCreateDto.getOrderId())).orElseThrow();
        orderItem.setOrderEntity(orderEntity);
        MenuItem menuItem = menuItemRepository.findById(UUID.fromString(orderItemCreateDto.getMenuItemId())).orElseThrow();
        orderItem.setMenuItem(menuItem);
        return orderItem;
    }

}
