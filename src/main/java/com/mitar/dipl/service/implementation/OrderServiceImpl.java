package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.OrderMapper;
import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.entity.Order;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;

    private UserRepository userRepository;

    private OrderMapper orderMapper;


    @Override
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getOrderById(String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));
        if (order.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(order.get());
    }

    @Override
    public ResponseEntity<?> getOrderByBillId(String billId) {
        Optional<Order> order = orderRepository.findByBill_Id(UUID.fromString(billId));
        if (order.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(order.get());
    }

    @Override
    public ResponseEntity<?> createOrder(OrderCreateDto orderCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderRepository.save(orderMapper.toEntity(orderCreateDto)));
    }

    @Override
    public ResponseEntity<?> deleteOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));
        if (order.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        orderRepository.delete(order.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> updateOrder(String orderId, OrderCreateDto orderCreateDto) {
        Optional<Order> optionalOrder = orderRepository.findById(UUID.fromString(orderId));
        if (optionalOrder.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        Order existingOrder = optionalOrder.get();

        existingOrder.setStatus(orderCreateDto.getStatus());
        existingOrder.setOrderItems(orderCreateDto.getOrderItems());
        Optional<User> user = userRepository.findById(UUID.fromString(orderCreateDto.getUserId()));
        user.ifPresent(existingOrder::setUser);

        return ResponseEntity.status(HttpStatus.OK).body(orderMapper.toDto(orderRepository.save(existingOrder)));
    }

}
