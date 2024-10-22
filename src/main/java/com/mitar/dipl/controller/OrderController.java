package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.service.MenuItemService;
import com.mitar.dipl.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(orderId));
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getOrderByBillId(@PathVariable String billId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByBillId(billId));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Validated OrderCreateDto orderCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderCreateDto));
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orderService.deleteOrder(orderId));
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestBody @Validated OrderCreateDto orderCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrder(orderId, orderCreateDto));
    }
}
