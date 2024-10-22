package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.order.OrderCreateDto;
import com.mitar.dipl.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("@securityUtils.isOrderOwnerByOrderId(#orderId) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(orderId));
    }

    @GetMapping("/bill/{billId}")
    @PreAuthorize("@securityUtils.isOrderOwnerByBillId(#billId) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getOrderByBillId(@PathVariable String billId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByBillId(billId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@securityUtils.isUserSelf(#userId) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<?> createOrder(@RequestBody @Validated OrderCreateDto orderCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderCreateDto));
    }

    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orderService.deleteOrder(orderId));
    }

    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("@securityUtils.isOrderOwnerByOrderId(#orderId) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancelOrder(orderId));
    }

    @PutMapping("/update/{orderId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestBody @Validated OrderCreateDto orderCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrder(orderId, orderCreateDto));
    }
}
