package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.OrderMapper;
import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.OrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<?> getAllOrders() {
        logger.info("Fetching all orders.");
        var orderDtos = orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .toList();
        return ResponseEntity.ok(orderDtos);
    }

    @Override
    public ResponseEntity<?> getOrderById(String orderId) {
        UUID parsedOrderId = UUID.fromString(orderId);

        Optional<OrderEntity> orderOpt = orderRepository.findById(parsedOrderId);
        if (orderOpt.isEmpty()) {
            logger.warn("Order not found with ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        OrderDto orderDto = orderMapper.toDto(orderOpt.get());
        logger.info("Retrieved Order ID: {}", orderId);
        return ResponseEntity.ok(orderDto);
    }

    @Override
    public ResponseEntity<?> getOrderByBillId(String billId) {
        UUID parsedBillId = UUID.fromString(billId);

        Optional<OrderEntity> orderOpt = orderRepository.findByBill_Id(parsedBillId);
        if (orderOpt.isEmpty()) {
            logger.warn("Order not found with Bill ID: {}", billId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found for the provided Bill ID.");
        }

        OrderDto orderDto = orderMapper.toDto(orderOpt.get());
        logger.info("Retrieved Order ID: {} by Bill ID: {}", orderOpt.get().getId(), billId);
        return ResponseEntity.ok(orderDto);
    }

    @Override
    public ResponseEntity<?> createOrder(OrderCreateDto orderCreateDto) {
        logger.info("Creating a new order.");
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(Status.PENDING);

        Set<UUID> menuItemUUIDs = orderCreateDto.getMenuItemIdsAndQuantities().keySet().stream()
                .map(UUIDUtils::parseUUID)
                .collect(Collectors.toSet());

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemUUIDs);
        if (menuItems.size() != menuItemUUIDs.size()) {
            Set<UUID> foundIds = menuItems.stream().map(MenuItem::getId).collect(Collectors.toSet());
            Set<UUID> notFoundIds = new HashSet<>(menuItemUUIDs);
            notFoundIds.removeAll(foundIds);
            logger.warn("MenuItems not found with IDs: {}", notFoundIds);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("MenuItems not found with the provided IDs: " + notFoundIds);
        }

        for (MenuItem menuItem : menuItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setOrderEntity(orderEntity);
            orderItem.setQuantity(orderCreateDto.getMenuItemIdsAndQuantities().get(menuItem.getId().toString()));
            orderItem.setPrice(menuItem.getPrice());
            orderEntity.addOrderItem(orderItem);
        }

        Optional<User> userOpt = userRepository.findById(UUID.fromString(orderCreateDto.getUserId()));
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {}", orderCreateDto.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found with the provided ID.");
        }
        orderEntity.setUser(userOpt.get());

        OrderEntity savedOrder = orderRepository.save(orderEntity);
        logger.info("Created Order ID: {}", savedOrder.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toDto(savedOrder));
    }

    @Override
    public ResponseEntity<?> deleteOrder(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);

        Optional<OrderEntity> orderOpt = orderRepository.findById(parsedOrderId);
        if (orderOpt.isEmpty()) {
            logger.warn("Order not found for deletion with ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        orderRepository.delete(orderOpt.get());
        logger.info("Deleted Order ID: {}", orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<?> updateOrder(String orderId, OrderCreateDto orderCreateDto) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);

        Optional<OrderEntity> optionalOrder = orderRepository.findById(parsedOrderId);
        if (optionalOrder.isEmpty()) {
            logger.warn("Order not found for update with ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        OrderEntity existingOrder = optionalOrder.get();

        if (!(existingOrder.getStatus() == Status.PENDING || existingOrder.getStatus() == Status.IN_PROGRESS)) {
            logger.warn("Attempt to update Order ID {} with status {}", orderId, existingOrder.getStatus());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Only orders with status PENDING or IN_PROGRESS can be updated.");
        }

        UUID userUuid = UUIDUtils.parseUUID(orderCreateDto.getUserId());

        Optional<User> userOpt = userRepository.findById(userUuid);
        if (userOpt.isEmpty()) {
            logger.warn("User not found with ID: {}", orderCreateDto.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found with the provided ID.");
        }
        existingOrder.setUser(userOpt.get());

        logger.info("Updating Order ID: {}", orderId);

        Map<String, Integer> items = orderCreateDto.getMenuItemIdsAndQuantities();

        Set<UUID> menuItemUUIDs = items.keySet().stream()
                .map(UUIDUtils::parseUUID)
                .collect(Collectors.toSet());

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemUUIDs);
        if (menuItems.size() != menuItemUUIDs.size()) {
            Set<UUID> foundIds = menuItems.stream().map(MenuItem::getId).collect(Collectors.toSet());
            Set<UUID> notFoundIds = new HashSet<>(menuItemUUIDs);
            notFoundIds.removeAll(foundIds);
            logger.warn("MenuItems not found with IDs: {}", notFoundIds);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("MenuItems not found with the provided IDs: " + notFoundIds);
        }

        Set<OrderItem> itemsToRemove = new HashSet<>(existingOrder.getOrderItems());
        for (OrderItem orderItem : itemsToRemove) {
            existingOrder.removeOrderItem(orderItem);
        }

        for (MenuItem menuItem : menuItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setOrderEntity(existingOrder);
            orderItem.setQuantity(items.get(menuItem.getId().toString()));
            orderItem.setPrice(menuItem.getPrice());
            existingOrder.addOrderItem(orderItem);
        }

        existingOrder.setStatus(Status.valueOf(orderCreateDto.getStatus()));

        OrderEntity updatedOrder = orderRepository.save(existingOrder);
        OrderDto orderDto = orderMapper.toDto(updatedOrder);
        logger.info("Updated Order ID: {}", orderId);
        return ResponseEntity.ok(orderDto);
    }
}
