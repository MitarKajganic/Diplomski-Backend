package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.model.dto.inventory.InventoryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InventoryService {

    List<InventoryDto> getInventories();

    InventoryDto getInventoryById(String inventoryId);

    InventoryDto getInventoryByIngredientName(String itemName);

    InventoryDto createInventory(InventoryCreateDto inventoryCreateDto);

    String deleteInventory(String inventoryId);

    InventoryDto updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto);

}
