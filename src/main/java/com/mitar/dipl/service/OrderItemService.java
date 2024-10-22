package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.model.dto.order_item.OrderItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderItemService {


    /**
     * Fetches all OrderItems.
     *
     * @return List of OrderItemDto
     */
    List<OrderItemDto> getAllOrderItems();


    /**
     * Fetches an OrderItem by its ID.
     *
     * @param orderItemId The UUID of the OrderItem as a string.
     * @return OrderItemDto
     */
    OrderItemDto getOrderItemById(String orderItemId);


    /**
     * Fetches OrderItems by their associated MenuItem ID.
     *
     * @param menuItemId The UUID of the MenuItem as a string.
     * @return List of OrderItemDto
     */
    List<OrderItemDto> getOrderItemsByMenuItemId(String menuItemId);


    /**
     * Creates a new OrderItem.
     *
     * @param orderItemCreateDto The DTO containing OrderItem creation data.
     * @return OrderItemDto
     */
    OrderItemDto createOrderItem(OrderItemCreateDto orderItemCreateDto);


    /**
     * Deletes an OrderItem by its ID.
     *
     * @param orderItemId The UUID of the OrderItem as a string.
     * @return Success message.
     */
    String deleteOrderItem(String orderItemId);


    /**
     * Updates an existing OrderItem.
     *
     * @param orderItemId          The UUID of the OrderItem as a string.
     * @param orderItemCreateDto The DTO containing updated OrderItem data.
     * @return OrderItemDto
     */
    OrderItemDto updateOrderItem(String orderItemId, OrderItemCreateDto orderItemCreateDto);

}
