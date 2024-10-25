package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.OrderItemMapper;
import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.OrderItem;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.service.OrderItemService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> getAllOrderItems() {
        log.info("Fetching all OrderItems.");
        List<OrderItemDto> orderItemDtos = orderItemRepository.findAll().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
        log.info("Fetched {} OrderItems.", orderItemDtos.size());
        return orderItemDtos;
    }

    @Override
    public OrderItemDto getOrderItemById(String orderItemId) {
        UUID parsedOrderItemId = UUIDUtils.parseUUID(orderItemId);
        log.debug("Fetching OrderItem with ID: {}", parsedOrderItemId);

        OrderItem orderItem = orderItemRepository.findById(parsedOrderItemId)
                .orElseThrow(() -> {
                    log.warn("OrderItem not found with ID: {}", orderItemId);
                    return new ResourceNotFoundException("OrderItem not found with ID: " + orderItemId);
                });

        OrderItemDto orderItemDto = orderItemMapper.toDto(orderItem);
        log.info("Fetched OrderItem: {}", orderItemDto);
        return orderItemDto;
    }

    @Override
    public List<OrderItemDto> getOrderItemsByMenuItemId(String menuItemId) {
        UUID parsedMenuItemId = UUIDUtils.parseUUID(menuItemId);
        log.debug("Fetching OrderItems with MenuItem ID: {}", parsedMenuItemId);

        List<OrderItem> orderItems = orderItemRepository.findAllByMenuItem_Id(parsedMenuItemId);
        List<OrderItemDto> orderItemDtos = orderItems.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());

        if (orderItemDtos.isEmpty()) {
            log.warn("No OrderItems found with MenuItem ID: {}", menuItemId);
            throw new ResourceNotFoundException("No OrderItems found with MenuItem ID: " + menuItemId);
        }

        log.info("Fetched {} OrderItems with MenuItem ID: {}", orderItemDtos.size(), menuItemId);
        return orderItemDtos;
    }

    @Override
    public OrderItemDto createOrderItem(OrderItemCreateDto orderItemCreateDto) {
        UUID orderId = UUIDUtils.parseUUID(orderItemCreateDto.getOrderId());
        UUID menuItemId = UUIDUtils.parseUUID(orderItemCreateDto.getMenuItemId());

        log.debug("Attempting to create OrderItem for Order ID: {} and MenuItem ID: {}", orderId, menuItemId);

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found with ID: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        MenuItem menuItemEntity = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with ID: {}", menuItemId);
                    return new ResourceNotFoundException("MenuItem not found with ID: " + menuItemId);
                });

        if (!(orderEntity.getStatus().equals(Status.PENDING) || orderEntity.getStatus().equals(Status.IN_PROGRESS))) {
            log.warn("Order status is not PENDING or IN_PROGRESS for Order ID: {}", orderId);
            throw new BadRequestException("Order status is not PENDING or IN_PROGRESS.");
        }

        Optional<OrderItem> existingOrderItemOpt = orderItemRepository.findByOrderEntityAndMenuItem(orderEntity, menuItemEntity);
        if (existingOrderItemOpt.isPresent()) {
            OrderItem existingOrderItem = existingOrderItemOpt.get();
            existingOrderItem.setQuantity(existingOrderItem.getQuantity() + orderItemCreateDto.getQuantity());
            existingOrderItem.setPrice(orderItemCreateDto.getPrice());
            existingOrderItem.setName(menuItemEntity.getName());
            orderItemRepository.save(existingOrderItem);
            log.info("Updated OrderItem ID: {} for Order ID: {}", existingOrderItem.getId(), orderId);
            return orderItemMapper.toDto(existingOrderItem);
        } else {
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setPrice(orderItemCreateDto.getPrice());
            newOrderItem.setQuantity(orderItemCreateDto.getQuantity());
            newOrderItem.setOrderEntity(orderEntity);
            newOrderItem.setMenuItem(menuItemEntity);
            newOrderItem.setName(menuItemEntity.getName());
            orderItemRepository.save(newOrderItem);
            log.info("Created new OrderItem ID: {} for Order ID: {}", newOrderItem.getId(), orderId);
            return orderItemMapper.toDto(newOrderItem);
        }
    }

    @Override
    public String deleteOrderItem(String orderItemId) {
        UUID parsedOrderItemId = UUIDUtils.parseUUID(orderItemId);
        log.debug("Attempting to delete OrderItem with ID: {}", parsedOrderItemId);

        OrderItem orderItem = orderItemRepository.findById(parsedOrderItemId)
                .orElseThrow(() -> {
                    log.warn("OrderItem not found with ID: {}", orderItemId);
                    return new ResourceNotFoundException("OrderItem not found with ID: " + orderItemId);
                });

        OrderEntity orderEntity = orderItem.getOrderEntity();

        if (!(orderEntity.getStatus().equals(Status.PENDING) || orderEntity.getStatus().equals(Status.IN_PROGRESS))) {
            log.warn("Order status is not PENDING or IN_PROGRESS for Order ID: {}", orderEntity.getId());
            throw new BadRequestException("Order status is not PENDING or IN_PROGRESS.");
        }

        orderEntity.removeOrderItem(orderItem);
        orderRepository.save(orderEntity);

        log.info("Deleted OrderItem ID: {} from Order ID: {}", orderItemId, orderEntity.getId());
        return "OrderItem deleted successfully.";
    }

    @Override
    public OrderItemDto updateOrderItem(String orderItemId, OrderItemCreateDto orderItemCreateDto) {
        UUID parsedOrderItemId = UUIDUtils.parseUUID(orderItemId);
        log.debug("Attempting to update OrderItem with ID: {}", parsedOrderItemId);

        OrderItem existingOrderItem = orderItemRepository.findById(parsedOrderItemId)
                .orElseThrow(() -> {
                    log.warn("OrderItem not found with ID: {}", orderItemId);
                    return new ResourceNotFoundException("OrderItem not found with ID: " + orderItemId);
                });

        UUID newMenuItemId = UUIDUtils.parseUUID(orderItemCreateDto.getMenuItemId());
        log.debug("Updating OrderItem ID: {} to MenuItem ID: {}", orderItemId, newMenuItemId);

        MenuItem newMenuItem = menuItemRepository.findById(newMenuItemId)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with ID: {}", newMenuItemId);
                    return new ResourceNotFoundException("MenuItem not found with ID: " + newMenuItemId);
                });

        OrderEntity currentOrderEntity = existingOrderItem.getOrderEntity();

        if (!currentOrderEntity.getId().toString().equals(orderItemCreateDto.getOrderId())) {
            log.warn("Order ID mismatch for OrderItem ID: {}", orderItemId);
            throw new BadRequestException("Order ID mismatch.");
        }

        if (!(currentOrderEntity.getStatus().equals(Status.PENDING) || currentOrderEntity.getStatus().equals(Status.IN_PROGRESS))) {
            log.warn("Order status is not PENDING or IN_PROGRESS for Order ID: {}", currentOrderEntity.getId());
            throw new BadRequestException("Order status is not PENDING or IN_PROGRESS.");
        }

        boolean menuItemChanged = !existingOrderItem.getMenuItem().getId().equals(newMenuItemId);

        if (menuItemChanged) {
            Optional<OrderItem> duplicateOrderItemOpt = orderItemRepository.findByOrderEntityAndMenuItem(currentOrderEntity, newMenuItem);

            if (duplicateOrderItemOpt.isPresent()) {
                OrderItem duplicateOrderItem = duplicateOrderItemOpt.get();
                duplicateOrderItem.setQuantity(duplicateOrderItem.getQuantity() + existingOrderItem.getQuantity());
                duplicateOrderItem.setPrice(orderItemCreateDto.getPrice());
                duplicateOrderItem.setName(newMenuItem.getName());
                orderItemRepository.save(duplicateOrderItem);

                currentOrderEntity.removeOrderItem(existingOrderItem);
                orderItemRepository.delete(existingOrderItem);

                log.info("Merged OrderItem ID: {} into existing OrderItem ID: {} in Order ID: {}",
                        existingOrderItem.getId(), duplicateOrderItem.getId(), currentOrderEntity.getId());

                return orderItemMapper.toDto(duplicateOrderItem);
            } else {
                existingOrderItem.setMenuItem(newMenuItem);
                existingOrderItem.setQuantity(orderItemCreateDto.getQuantity());
                existingOrderItem.setPrice(orderItemCreateDto.getPrice());
                existingOrderItem.setName(newMenuItem.getName());
                orderItemRepository.save(existingOrderItem);
                log.info("Updated MenuItem for OrderItem ID: {} to MenuItem ID: {}",
                        existingOrderItem.getId(), newMenuItemId);
                return orderItemMapper.toDto(existingOrderItem);
            }
        } else {
            existingOrderItem.setQuantity(orderItemCreateDto.getQuantity());
            existingOrderItem.setPrice(orderItemCreateDto.getPrice());
            existingOrderItem.setName(newMenuItem.getName());

            OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
            log.info("Updated OrderItem ID: {}", updatedOrderItem.getId());

            return orderItemMapper.toDto(updatedOrderItem);
        }
    }
}
