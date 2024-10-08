package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.Order;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OrderItemMapper {

    private OrderRepository orderRepository;

    private MenuItemRepository menuItemRepository;

    public OrderItemDto toDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId().toString());
        orderItemDto.setPrice(orderItem.getPrice().toString());
        orderItemDto.setQuantity(String.valueOf(orderItem.getQuantity()));
        orderItemDto.setOrderId(orderItem.getOrder().getId().toString());
        orderItemDto.setMenuItemId(orderItem.getMenuItem().getId().toString());
        return orderItemDto;
    }

    public OrderItem toEntity(OrderItemCreateDto orderItemCreateDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal(orderItemCreateDto.getPrice()));
        orderItem.setQuantity(Integer.parseInt(orderItemCreateDto.getQuantity()));
        Order order = orderRepository.findById(UUID.fromString(orderItemCreateDto.getOrderId())).orElseThrow();
        orderItem.setOrder(order);
        MenuItem menuItem = menuItemRepository.findById(UUID.fromString(orderItemCreateDto.getMenuItemId())).orElseThrow();
        orderItem.setMenuItem(menuItem);
        return orderItem;
    }

}