package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.OrderMapper;
import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;
import com.mitar.dipl.model.entity.*;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.OrderService;
import com.mitar.dipl.utils.UUIDUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderDto> getAllOrders() {
        log.info("Fetching all orders.");
        List<OrderDto> orderDtos = orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .toList();
        log.info("Fetched {} orders.", orderDtos.size());
        return orderDtos;
    }

    @Override
    public OrderDto getOrderById(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        log.debug("Fetching Order with ID: {}", parsedOrderId);

        OrderEntity orderEntity = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> {
                    log.warn("Order not found with ID: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        OrderDto orderDto = orderMapper.toDto(orderEntity);
        log.info("Retrieved Order ID: {}", orderId);
        return orderDto;
    }

    @Override
    public OrderDto getOrderByBillId(String billId) {
        UUID parsedBillId = UUIDUtils.parseUUID(billId);
        log.debug("Fetching Order with Bill ID: {}", parsedBillId);

        OrderEntity orderEntity = orderRepository.findByBill_Id(parsedBillId)
                .orElseThrow(() -> {
                    log.warn("Order not found with Bill ID: {}", billId);
                    return new ResourceNotFoundException("Order not found for the provided Bill ID: " + billId);
                });

        OrderDto orderDto = orderMapper.toDto(orderEntity);
        log.info("Retrieved Order ID: {} by Bill ID: {}", orderEntity.getId(), billId);
        return orderDto;
    }

    @Override
    public List<OrderDto> getOrdersByUserId(String userId) {
        UUID parsedUserId = UUIDUtils.parseUUID(userId);
        log.debug("Fetching Orders for User with ID: {}", parsedUserId);

        List<OrderDto> orderDtos = orderRepository.findAllByUser_Id(parsedUserId).stream()
                .map(orderMapper::toDto)
                .toList();
        log.info("Retrieved {} Orders for User ID: {}", orderDtos.size(), userId);
        return orderDtos;
    }

    @Override
    public OrderDto createOrder(OrderCreateDto orderCreateDto) {
        log.info("Creating a new order.");
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
            log.warn("MenuItems not found with IDs: {}", notFoundIds);
            throw new BadRequestException("MenuItems not found with the provided IDs: " + notFoundIds);
        }

        for (MenuItem menuItem : menuItems) {
            Integer quantity = orderCreateDto.getMenuItemIdsAndQuantities().get(menuItem.getId().toString());
            if (quantity == null || quantity <= 0) {
                log.warn("Invalid quantity for MenuItem ID: {}", menuItem.getId());
                throw new BadRequestException("Invalid quantity for MenuItem ID: " + menuItem.getId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setOrderEntity(orderEntity);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
            orderItem.setName(menuItem.getName());
            orderEntity.addOrderItem(orderItem);
        }

        UUID userUuid = UUIDUtils.parseUUID(orderCreateDto.getUserId());
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", orderCreateDto.getUserId());
                    return new BadRequestException("User not found with the provided ID: " + orderCreateDto.getUserId());
                });
        orderEntity.setUser(user);

        DeliveryInfo deliveryInfo = orderCreateDto.getDeliveryInfo();

        if (deliveryInfo.getStreet().length() < 3 || deliveryInfo.getStreet().length() > 50) {
            log.warn("Invalid street length for Order ID: {}", orderEntity.getId());
            throw new BadRequestException("Street length must be between 3 and 50 characters.");
        }

        if (deliveryInfo.getNumber().isEmpty() || deliveryInfo.getNumber().length() > 10) {
            log.warn("Invalid street number length for Order ID: {}", orderEntity.getId());
            throw new BadRequestException("Street number length must be between 1 and 10 characters.");
        }

        if (deliveryInfo.getFloor() != null && (deliveryInfo.getFloor() < 0 || deliveryInfo.getFloor() > 100)) {
            log.warn("Invalid floor number for Order ID: {}", orderEntity.getId());
            throw new BadRequestException("Floor number must be between 0 and 100.");
        }

        if (!deliveryInfo.getPhoneNumber().matches("^[0-9]{8,11}$")) {
            log.warn("Invalid phone number for Order ID: {}", orderEntity.getId());
            throw new BadRequestException("Phone number must contain between 8 and 11 numbers.");
        }

        orderEntity.setDeliveryInfo(orderCreateDto.getDeliveryInfo());

        OrderEntity savedOrder = orderRepository.save(orderEntity);
        entityManager.flush();
        entityManager.refresh(savedOrder);
        log.info("Created Order ID: {}", savedOrder.getId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public String deleteOrder(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        log.debug("Attempting to delete Order with ID: {}", parsedOrderId);

        OrderEntity orderEntity = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for deletion with ID: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        if (orderEntity.getStatus() == Status.COMPLETED || orderEntity.getStatus() == Status.CANCELLED) {
            log.warn("Cannot delete Order ID: {} with status: {}", orderId, orderEntity.getStatus());
            throw new BadRequestException("Cannot delete Order with status: " + orderEntity.getStatus());
        }

        orderRepository.delete(orderEntity);
        log.info("Deleted Order ID: {}", orderId);
        return "Order deleted successfully.";
    }

    @Override
    public String cancelOrder(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        log.debug("Attempting to cancel Order with ID: {}", parsedOrderId);

        OrderEntity orderEntity = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for cancellation with ID: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        if (orderEntity.getStatus() == Status.COMPLETED || orderEntity.getStatus() == Status.CANCELLED) {
            log.warn("Cannot cancel Order ID: {} with status: {}", orderId, orderEntity.getStatus());
            throw new BadRequestException("Cannot cancel Order with status: " + orderEntity.getStatus());
        }

        orderEntity.setStatus(Status.CANCELLED);
        orderRepository.save(orderEntity);
        log.info("Cancelled Order ID: {}", orderId);
        return "Order cancelled successfully.";
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderCreateDto orderCreateDto) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        log.debug("Attempting to update Order with ID: {}", parsedOrderId);

        OrderEntity existingOrder = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for update with ID: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        if (!(existingOrder.getStatus() == Status.PENDING || existingOrder.getStatus() == Status.IN_PROGRESS)) {
            log.warn("Attempt to update Order ID {} with status {}", orderId, existingOrder.getStatus());
            throw new BadRequestException("Only orders with status PENDING or IN_PROGRESS can be updated.");
        }

        if (orderCreateDto.getUserId() != null && !orderCreateDto.getUserId().isEmpty()) {
            UUID userUuid = UUIDUtils.parseUUID(orderCreateDto.getUserId());
            User user = userRepository.findById(userUuid)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", orderCreateDto.getUserId());
                        return new BadRequestException("User not found with the provided ID: " + orderCreateDto.getUserId());
                    });
            existingOrder.setUser(user);
            log.debug("Updated User for Order ID: {}", orderId);
        }

        if (orderCreateDto.getMenuItemIdsAndQuantities() != null && !orderCreateDto.getMenuItemIdsAndQuantities().isEmpty()) {
            Map<String, Integer> items = orderCreateDto.getMenuItemIdsAndQuantities();

            Set<UUID> menuItemUUIDs = items.keySet().stream()
                    .map(UUIDUtils::parseUUID)
                    .collect(Collectors.toSet());

            List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemUUIDs);
            if (menuItems.size() != menuItemUUIDs.size()) {
                Set<UUID> foundIds = menuItems.stream().map(MenuItem::getId).collect(Collectors.toSet());
                Set<UUID> notFoundIds = new HashSet<>(menuItemUUIDs);
                notFoundIds.removeAll(foundIds);
                log.warn("MenuItems not found with IDs: {}", notFoundIds);
                throw new BadRequestException("MenuItems not found with the provided IDs: " + notFoundIds);
            }

            existingOrder.getOrderItems().stream()
                    .toList()
                    .forEach(existingOrder::removeOrderItem);
            log.debug("Cleared existing OrderItems for Order ID: {}", orderId);

            for (MenuItem menuItem : menuItems) {
                Integer quantity = items.get(menuItem.getId().toString());

                OrderItem orderItem = new OrderItem();
                orderItem.setMenuItem(menuItem);
                orderItem.setOrderEntity(existingOrder);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(quantity)));

                existingOrder.addOrderItem(orderItem);
            }
            log.debug("Associated new OrderItems for Order ID: {}", orderId);
        }

        existingOrder.setDeliveryInfo(orderCreateDto.getDeliveryInfo());

        if (orderCreateDto.getStatus() != null) {
            Status newStatus = Status.valueOf(orderCreateDto.getStatus().toUpperCase());
            existingOrder.setStatus(newStatus);
            log.debug("Updated status to {} for Order ID: {}", newStatus, orderId);
        }

        OrderEntity updatedOrder = orderRepository.save(existingOrder);
        log.info("Updated Order ID: {}", orderId);
        return orderMapper.toDto(updatedOrder);
    }

}
