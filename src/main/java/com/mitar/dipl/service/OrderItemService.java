package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderItemService {

    List<OrderItemDto> getAllOrderItems();

    OrderItemDto getOrderItemById(String orderItemId);

    List<OrderItemDto> getOrderItemsByMenuItemId(String menuItemId);

    OrderItemDto createOrderItem(OrderItemCreateDto orderItemCreateDto);

    String deleteOrderItem(String orderItemId);

    OrderItemDto updateOrderItem(String orderItemId, OrderItemCreateDto orderItemCreateDto);

}
