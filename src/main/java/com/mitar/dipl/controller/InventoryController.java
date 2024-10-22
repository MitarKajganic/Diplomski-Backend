package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;


    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getInventories() {
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.getInventories());
    }

    @GetMapping("/{inventoryId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getInventoryById(@PathVariable String inventoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.getInventoryById(inventoryId));
    }

    @GetMapping("/item/{itemName}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getInventoryByItemName(@PathVariable String itemName) {
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.getInventoryByIngredientName(itemName));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createInventory(@RequestBody @Validated InventoryCreateDto inventoryCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(inventoryCreateDto));
    }

    @DeleteMapping("/delete/{inventoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteInventory(@PathVariable String inventoryId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(inventoryService.deleteInventory(inventoryId));
    }

    @PutMapping("/update/{inventoryId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> updateInventory(@PathVariable String inventoryId, @RequestBody @Validated InventoryCreateDto inventoryCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.updateInventory(inventoryId, inventoryCreateDto));
    }

}
