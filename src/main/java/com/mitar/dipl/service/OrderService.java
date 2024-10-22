package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAllOrders();

    OrderDto getOrderById(String orderId);

    OrderDto getOrderByBillId(String billId);

    OrderDto createOrder(OrderCreateDto orderCreateDto);

    String deleteOrder(String orderId);

    OrderDto updateOrder(String orderId, OrderCreateDto orderCreateDto);

}
