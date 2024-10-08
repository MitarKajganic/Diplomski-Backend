package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import org.springframework.http.ResponseEntity;

public interface InventoryService {

    ResponseEntity<?> getInventoryById(String inventoryId);

    ResponseEntity<?> getInventoryByIngredientName(String ingredientName);

    ResponseEntity<?> deleteInventory(String inventoryId);

    ResponseEntity<?> createInventory(InventoryCreateDto inventoryCreateDto);

    ResponseEntity<?> updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto);

}
