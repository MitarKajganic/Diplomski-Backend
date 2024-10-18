package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.security.CheckSecurity;
import com.mitar.dipl.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;


    @GetMapping
//    @CheckSecurity(roles = {"SUPER_ADMIN, ADMIN, STAFF"})
    public ResponseEntity<?> getInventories() {
        return inventoryService.getInventories();
    }

    @GetMapping("/{inventoryId}")
//    @CheckSecurity(roles = {"SUPER_ADMIN, ADMIN, STAFF"})
    public ResponseEntity<?> getInventoryById(@PathVariable String inventoryId) {
        return inventoryService.getInventoryById(inventoryId);
    }

    @GetMapping("/item/{itemName}")
//    @CheckSecurity(roles = {"SUPER_ADMIN, ADMIN, STAFF"})
    public ResponseEntity<?> getInventoryByItemName(@PathVariable String itemName) {
        return inventoryService.getInventoryByIngredientName(itemName);
    }

    @PostMapping
//    @CheckSecurity(roles = {"SUPER_ADMIN, ADMIN"})
    public ResponseEntity<?> createInventory(@RequestBody @Validated InventoryCreateDto inventoryCreateDto) {
        return inventoryService.createInventory(inventoryCreateDto);
    }

    @DeleteMapping("/delete/{inventoryId}")
//    @CheckSecurity(roles = {"SUPER_ADMIN, ADMIN"})
    public ResponseEntity<?> deleteInventory(@PathVariable String inventoryId) {
        return inventoryService.deleteInventory(inventoryId);
    }

    @PutMapping("/update/{inventoryId}")
//    @CheckSecurity(roles = {"SUPER_ADMIN, ADMIN"})
    public ResponseEntity<?> updateInventory(@PathVariable String inventoryId, @RequestBody @Validated InventoryCreateDto inventoryCreateDto) {
        return inventoryService.updateInventory(inventoryId, inventoryCreateDto);
    }

}
