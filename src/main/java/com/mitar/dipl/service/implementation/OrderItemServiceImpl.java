package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.OrderItemMapper;
import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.service.OrderItemService;
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
public class OrderItemServiceImpl implements OrderItemService {

    private OrderItemRepository orderItemRepository;

    private OrderRepository orderRepository;

    private MenuItemRepository menuItemRepository;

    private OrderItemMapper orderItemMapper;


    @Override
    public ResponseEntity<?> getAllOrderItems() {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getOrderItemById(String orderItemId) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(UUID.fromString(orderItemId));
        if (orderItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(orderItem.get());
    }

    @Override
    public ResponseEntity<?> getOrderItemsByMenuItemId(String menuItemId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemRepository.findAllByMenuItem_Id(UUID.fromString(menuItemId)));
    }

    @Override
    public ResponseEntity<?> createOrderItem(OrderItemCreateDto orderItemCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemRepository.save(orderItemMapper.toEntity(orderItemCreateDto)));
    }

    @Override
    public ResponseEntity<?> deleteOrderItem(String orderItemId) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(UUID.fromString(orderItemId));
        if (orderItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        orderItemRepository.delete(orderItem.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> updateOrderItem(String orderItemId, OrderItemCreateDto orderItemCreateDto) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(UUID.fromString(orderItemId));
        if (optionalOrderItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        OrderItem orderItem = optionalOrderItem.get();

        orderItem.setPrice(orderItemCreateDto.getPrice());
        orderItem.setQuantity(orderItemCreateDto.getQuantity());
        Optional<OrderEntity> order = orderRepository.findById(UUID.fromString(orderItemCreateDto.getOrderId()));
        if (order.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        orderItem.setOrderEntity(order.get());
        Optional<MenuItem> menuItem = menuItemRepository.findById(UUID.fromString(orderItemCreateDto.getMenuItemId()));
        if (menuItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        orderItem.setMenuItem(menuItem.get());

        return ResponseEntity.status(HttpStatus.OK).body(orderItemRepository.save(orderItem));
    }
}
