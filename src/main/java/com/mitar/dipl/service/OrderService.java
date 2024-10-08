package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import org.springframework.http.ResponseEntity;

public interface OrderService {

    ResponseEntity<?> getAllOrders();

    ResponseEntity<?> getOrderById(String orderId);

    ResponseEntity<?> getOrderByBillId(String billId);

    ResponseEntity<?> createOrder(OrderCreateDto orderCreateDto);

    ResponseEntity<?> deleteOrder(String orderId);

    ResponseEntity<?> updateOrder(String orderId, OrderCreateDto orderCreateDto);

}
