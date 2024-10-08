package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import org.springframework.http.ResponseEntity;

public interface OrderItemService {

    ResponseEntity<?> getAllOrderItems();

    ResponseEntity<?> getOrderItemById(String orderItemId);

    ResponseEntity<?> getOrderItemsByMenuItemId(String menuItemId);

    ResponseEntity<?> createOrderItem(OrderItemCreateDto orderItemCreateDto);

    ResponseEntity<?> deleteOrderItem(String orderItemId);

    ResponseEntity<?> updateOrderItem(String orderItemId, OrderItemCreateDto orderItemCreateDto);

}
