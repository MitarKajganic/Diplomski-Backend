package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.order_item.OrderItemCreateDto;
import com.mitar.dipl.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrderItems() {
        return orderItemService.getAllOrderItems();
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<?> getOrderItemById(@PathVariable String orderItemId) {
        return orderItemService.getOrderItemById(orderItemId);
    }

    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<?> getOrderItemsByMenuItemId(@PathVariable String menuItemId) {
        return orderItemService.getOrderItemsByMenuItemId(menuItemId);
    }

    @PostMapping
    public ResponseEntity<?> createOrderItem(@RequestBody @Validated OrderItemCreateDto orderItemCreateDto) {
        return orderItemService.createOrderItem(orderItemCreateDto);
    }

    @DeleteMapping("/delete/{orderItemId}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable String orderItemId) {
        return orderItemService.deleteOrderItem(orderItemId);
    }

    @PutMapping("/update/{orderItemId}")
    public ResponseEntity<?> updateOrderItem(@PathVariable String orderItemId, @RequestBody @Validated OrderItemCreateDto orderItemCreateDto) {
        return orderItemService.updateOrderItem(orderItemId, orderItemCreateDto);
    }

}
