package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.model.dto.inventory.InventoryDto;
import com.mitar.dipl.model.entity.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InventoryMapper {

    public InventoryDto toDto(Inventory inventory) {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setId(inventory.getId().toString());
        inventoryDto.setItemName(inventory.getItemName());
        inventoryDto.setQuantity(String.valueOf(inventory.getQuantity()));
        inventoryDto.setUnit(inventory.getUnit());
        inventoryDto.setLowStock(String.valueOf(inventory.isLowStock()));
        return inventoryDto;
    }

    public Inventory toEntity(InventoryCreateDto inventoryCreateDto) {
        Inventory inventory = new Inventory();
        inventory.setItemName(inventoryCreateDto.getItemName());
        inventory.setQuantity(0);
        inventory.setUnit(inventoryCreateDto.getUnit());
        inventory.setLowStock(false);
        return inventory;
    }

}