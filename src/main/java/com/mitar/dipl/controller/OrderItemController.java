package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getAllOrderItems() {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemService.getAllOrderItems());
    }

    @GetMapping("/{orderItemId}")
    @PreAuthorize("@securityUtils.isOrderItemOwnerByOrderItemId(#orderItemId) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getOrderItemById(@PathVariable String orderItemId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemService.getOrderItemById(orderItemId));
    }

    @GetMapping("/menu-item/{menuItemId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getOrderItemsByMenuItemId(@PathVariable String menuItemId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemService.getOrderItemsByMenuItemId(menuItemId));
    }

    @PostMapping
    @PreAuthorize("@securityUtils.isOrderItemOwnerByOrderId(#orderItemCreateDto.orderId)")
    public ResponseEntity<?> createOrderItem(@RequestBody @Validated OrderItemCreateDto orderItemCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.createOrderItem(orderItemCreateDto));
    }

    @DeleteMapping("/delete/{orderItemId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> deleteOrderItem(@PathVariable String orderItemId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orderItemService.deleteOrderItem(orderItemId));
    }

    @PutMapping("/update/{orderItemId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> updateOrderItem(@PathVariable String orderItemId, @RequestBody @Validated OrderItemCreateDto orderItemCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemService.updateOrderItem(orderItemId, orderItemCreateDto));
    }

}
