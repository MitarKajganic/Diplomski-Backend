package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.model.dto.order.OrderDto;

import java.util.List;

public interface OrderService {


    /**
     * Fetches all orders.
     *
     * @return List of OrderDto
     */
    List<OrderDto> getAllOrders();


    /**
     * Fetches an order by its ID.
     *
     * @param orderId The UUID of the order as a string.
     * @return OrderDto
     */
    OrderDto getOrderById(String orderId);


    /**
     * Fetches an order by its Bill ID.
     *
     * @param billId The UUID of the bill as a string.
     * @return OrderDto
     */
    OrderDto getOrderByBillId(String billId);

    /**
     * Fetches all orders by user ID.
     *
     * @param userId The UUID of the user as a string.
     * @return List of OrderDto
     */
    List<OrderDto> getOrdersByUserId(String userId);

    /**
     * Creates a new order.
     *
     * @param orderCreateDto The DTO containing order creation data.
     * @return OrderDto
     */
    OrderDto createOrder(OrderCreateDto orderCreateDto);


    /**
     * Deletes an order by its ID.
     *
     * @param orderId The UUID of the order as a string.
     * @return Success message.
     */
    String deleteOrder(String orderId);

    /**
     * Cancels an order by its ID.
     *
     * @param orderId The UUID of the order as a string.
     * @return Success message.
     */
    String cancelOrder(String orderId);


    /**
     * Updates an existing order.
     *
     * @param orderId         The UUID of the order as a string.
     * @param orderCreateDto The DTO containing updated order data.
     * @return OrderDto
     */
    OrderDto updateOrder(String orderId, OrderCreateDto orderCreateDto);

}
