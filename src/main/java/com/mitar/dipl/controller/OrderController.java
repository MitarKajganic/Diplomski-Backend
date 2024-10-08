package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.service.MenuItemService;
import com.mitar.dipl.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/menu-items")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getOrderByBillId(@PathVariable String billId) {
        return orderService.getOrderByBillId(billId);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Validated OrderCreateDto orderCreateDto) {
        return orderService.createOrder(orderCreateDto);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        return orderService.deleteOrder(orderId);
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestBody @Validated OrderCreateDto orderCreateDto) {
        return orderService.updateOrder(orderId, orderCreateDto);
    }
}
