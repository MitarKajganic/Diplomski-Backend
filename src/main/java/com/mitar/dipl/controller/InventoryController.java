package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
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

    @GetMapping("/{inventoryId}")
    public ResponseEntity<?> getInventoryById(@PathVariable String inventoryId) {
        return inventoryService.getInventoryById(inventoryId);
    }

    @GetMapping("/item/{ingredientName}")
    public ResponseEntity<?> getInventoryByIngredientName(@PathVariable String ingredientName) {
        return inventoryService.getInventoryByIngredientName(ingredientName);
    }

    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody @Validated InventoryCreateDto inventoryCreateDto) {
        return inventoryService.createInventory(inventoryCreateDto);
    }

    @DeleteMapping("/delete/{inventoryId}")
    public ResponseEntity<?> deleteInventory(@PathVariable String inventoryId) {
        return inventoryService.deleteInventory(inventoryId);
    }

    @PutMapping("/update/{inventoryId}")
    public ResponseEntity<?> updateInventory(@PathVariable String inventoryId, @RequestBody @Validated InventoryCreateDto inventoryCreateDto) {
        return inventoryService.updateInventory(inventoryId, inventoryCreateDto);
    }

}
