package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import org.springframework.http.ResponseEntity;

public interface InventoryService {

    ResponseEntity<?> getInventories();

    ResponseEntity<?> getInventoryById(String inventoryId);

    ResponseEntity<?> getInventoryByIngredientName(String itemName);

    ResponseEntity<?> createInventory(InventoryCreateDto inventoryCreateDto);

    ResponseEntity<?> deleteInventory(String inventoryId);

    ResponseEntity<?> updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto);

}
