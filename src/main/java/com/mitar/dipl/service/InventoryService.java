package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.model.dto.inventory.InventoryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InventoryService {

    /**
     * Fetches all inventory items.
     *
     * @return List of InventoryDto
     */
    List<InventoryDto> getInventories();

    /**
     * Fetches an inventory item by its ID.
     *
     * @param inventoryId The UUID of the inventory item as a string.
     * @return InventoryDto
     */
    InventoryDto getInventoryById(String inventoryId);

    /**
     * Fetches an inventory item by its ingredient name.
     *
     * @param itemName The name of the ingredient.
     * @return InventoryDto
     */
    InventoryDto getInventoryByIngredientName(String itemName);

    /**
     * Creates a new inventory item.
     *
     * @param inventoryCreateDto The InventoryCreateDto object.
     * @return InventoryDto
     */
    InventoryDto createInventory(InventoryCreateDto inventoryCreateDto);

    /**
     * Deletes an inventory item by its ID.
     *
     * @param inventoryId The UUID of the inventory item as a string.
     * @return Success message.
     */
    String deleteInventory(String inventoryId);

    /**
     * Updates an inventory item by its ID.
     *
     * @param inventoryId The UUID of the inventory item as a string.
     * @param inventoryCreateDto The DTO containing updated inventory data.
     * @return InventoryDto
     */
    InventoryDto updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto);

}
