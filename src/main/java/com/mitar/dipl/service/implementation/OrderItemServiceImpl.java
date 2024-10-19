package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.OrderItemMapper;
import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.service.OrderItemService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImpl.class);


    @Override
    public ResponseEntity<?> getAllOrderItems() {
        logger.info("Getting all OrderItems");
        return ResponseEntity.status(HttpStatus.OK).body(orderItemRepository.findAll().stream()
                .map(orderItemMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getOrderItemById(String orderItemId) {
        UUID parsedOrderItemId = UUID.fromString(orderItemId);

        Optional<OrderItem> orderItem = orderItemRepository.findById(parsedOrderItemId);
        if (orderItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(orderItemMapper.toDto(orderItem.get()));
    }

    @Override
    public ResponseEntity<?> getOrderItemsByMenuItemId(String menuItemId) {
        UUID parsedMenuItemId = UUID.fromString(menuItemId);

        return ResponseEntity.status(HttpStatus.OK).body(
                orderItemRepository.findAllByMenuItem_Id(parsedMenuItemId).stream()
                        .map(orderItemMapper::toDto)
                        .toList());
    }

    @Override
    public ResponseEntity<?> createOrderItem(OrderItemCreateDto orderItemCreateDto) {
        UUID orderId = UUID.fromString(orderItemCreateDto.getOrderId());
        UUID menuItemId = UUID.fromString(orderItemCreateDto.getMenuItemId());

        Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            logger.warn("Order not found with ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(menuItemId);
        if (menuItemOpt.isEmpty()) {
            logger.warn("MenuItem not found with ID: {}", menuItemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");
        }

        OrderEntity orderEntity = orderOpt.get();
        MenuItem menuItemEntity = menuItemOpt.get();

        if (!(orderEntity.getStatus().equals(Status.PENDING) || orderEntity.getStatus().equals(Status.IN_PROGRESS))) {
            logger.warn("Order status is not PENDING or IN_PROGRESS for Order ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order status is not PENDING or IN_PROGRESS.");
        }

        Optional<OrderItem> existingOrderItemOpt = orderItemRepository.findByOrderEntityAndMenuItem(orderEntity, menuItemEntity);
        if (existingOrderItemOpt.isPresent()) {
            OrderItem existingOrderItem = existingOrderItemOpt.get();
            existingOrderItem.setQuantity(existingOrderItem.getQuantity() + orderItemCreateDto.getQuantity());
            existingOrderItem.setPrice(orderItemCreateDto.getPrice());
            orderItemRepository.save(existingOrderItem);
            logger.info("Updated OrderItem ID: {} for Order ID: {}", existingOrderItem.getId(), orderId);
            return ResponseEntity.status(HttpStatus.OK).body(orderItemMapper.toDto(existingOrderItem));
        } else {
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setPrice(orderItemCreateDto.getPrice());
            newOrderItem.setQuantity(orderItemCreateDto.getQuantity());
            newOrderItem.setOrderEntity(orderEntity);
            newOrderItem.setMenuItem(menuItemEntity);
            orderItemRepository.save(newOrderItem);
            logger.info("Created new OrderItem ID: {} for Order ID: {}", newOrderItem.getId(), orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderItemMapper.toDto(newOrderItem));
        }
    }

    @Override
    public ResponseEntity<?> deleteOrderItem(String orderItemId) {
        UUID parsedOrderItemId = UUID.fromString(orderItemId);

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(parsedOrderItemId);
        if (orderItemOpt.isEmpty()) {
            logger.warn("OrderItem not found with ID: {}", orderItemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OrderItem not found.");
        }

        OrderItem orderItem = orderItemOpt.get();

        OrderEntity orderEntity = orderItem.getOrderEntity();

        if (!(orderEntity.getStatus().equals(Status.PENDING) || orderEntity.getStatus().equals(Status.IN_PROGRESS))) {
            logger.warn("Order status is not PENDING or IN_PROGRESS for Order ID: {}", orderEntity.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order status is not PENDING or IN_PROGRESS.");
        }

        orderEntity.removeOrderItem(orderItem);

        orderRepository.save(orderEntity);

        logger.info("Deleted OrderItem ID: {} from Order ID: {}", orderItemId, orderEntity.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<?> updateOrderItem(String orderItemId, OrderItemCreateDto orderItemCreateDto) {
        UUID parsedOrderItemId = UUID.fromString(orderItemId);

        Optional<OrderItem> existingOrderItemOpt = orderItemRepository.findById(parsedOrderItemId);
        if (existingOrderItemOpt.isEmpty()) {
            logger.warn("OrderItem not found with ID: {}", orderItemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OrderItem not found.");
        }
        OrderItem existingOrderItem = existingOrderItemOpt.get();

        UUID newMenuItemId = UUID.fromString(orderItemCreateDto.getMenuItemId());

        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(newMenuItemId);
        if (menuItemOpt.isEmpty()) {
            logger.warn("MenuItem not found with ID: {}", newMenuItemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");
        }
        MenuItem newMenuItem = menuItemOpt.get();
        OrderEntity currentOrderEntity = existingOrderItem.getOrderEntity();

        if (!currentOrderEntity.getId().toString().equals(orderItemCreateDto.getOrderId())) {
            logger.warn("Order ID mismatch for OrderItem ID: {}", orderItemId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order ID mismatch.");
        }

        if (!(currentOrderEntity.getStatus().equals(Status.PENDING) || currentOrderEntity.getStatus().equals(Status.IN_PROGRESS))) {
            logger.warn("Order status is not PENDING or IN_PROGRESS for Order ID: {}", currentOrderEntity.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order status is not PENDING or IN_PROGRESS.");
        }

        boolean menuItemChanged = !existingOrderItem.getMenuItem().getId().equals(newMenuItemId);

        if (menuItemChanged) {
            Optional<OrderItem> duplicateOrderItemOpt = orderItemRepository.findByOrderEntityAndMenuItem(currentOrderEntity, newMenuItem);

            if (duplicateOrderItemOpt.isPresent()) {
                OrderItem duplicateOrderItem = duplicateOrderItemOpt.get();
                duplicateOrderItem.setQuantity(duplicateOrderItem.getQuantity() + existingOrderItem.getQuantity());
                duplicateOrderItem.setPrice(orderItemCreateDto.getPrice());
                orderItemRepository.save(duplicateOrderItem);

                currentOrderEntity.removeOrderItem(existingOrderItem);
                orderItemRepository.delete(existingOrderItem);

                logger.info("Merged OrderItem ID: {} into existing OrderItem ID: {} in Order ID: {}",
                        existingOrderItem.getId(), duplicateOrderItem.getId(), currentOrderEntity.getId());

                return ResponseEntity.status(HttpStatus.OK).body(orderItemMapper.toDto(duplicateOrderItem));
            } else {
                existingOrderItem.setMenuItem(newMenuItem);
                existingOrderItem.setQuantity(orderItemCreateDto.getQuantity());
                existingOrderItem.setPrice(orderItemCreateDto.getPrice());
                orderItemRepository.save(existingOrderItem);
                logger.info("Updated MenuItem for OrderItem ID: {} to MenuItem ID: {}",
                        existingOrderItem.getId(), newMenuItemId);
                return ResponseEntity.status(HttpStatus.OK).body(orderItemMapper.toDto(existingOrderItem));
            }
        } else {
            existingOrderItem.setQuantity(orderItemCreateDto.getQuantity());
            existingOrderItem.setPrice(orderItemCreateDto.getPrice());

            OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
            logger.info("Updated OrderItem ID: {}", updatedOrderItem.getId());

            return ResponseEntity.status(HttpStatus.OK).body(orderItemMapper.toDto(updatedOrderItem));
        }
    }

}
