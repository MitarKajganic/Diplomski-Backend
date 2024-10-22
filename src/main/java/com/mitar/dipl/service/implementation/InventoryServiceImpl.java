package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.InventoryMapper;
import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.model.dto.inventory.InventoryDto;
import com.mitar.dipl.model.entity.Inventory;
import com.mitar.dipl.repository.InventoryRepository;
import com.mitar.dipl.service.InventoryService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;


    @Override
    public List<InventoryDto> getInventories() {
        log.info("Fetching all inventories.");
        List<InventoryDto> inventoryDtos = inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDto)
                .toList();
        log.info("Fetched {} inventories.", inventoryDtos.size());
        return inventoryDtos;
    }

    @Override
    public InventoryDto getInventoryById(String inventoryId) {
        UUID parsedId = UUIDUtils.parseUUID(inventoryId);
        log.debug("Fetching inventory with ID: {}", parsedId);

        Inventory inventory = inventoryRepository.findById(parsedId)
                .orElseThrow(() -> {
                    log.warn("Inventory not found with ID: {}", inventoryId);
                    return new ResourceNotFoundException("Inventory not found with ID: " + inventoryId);
                });

        InventoryDto inventoryDto = inventoryMapper.toDto(inventory);
        log.info("Fetched inventory: {}", inventoryDto);
        return inventoryDto;
    }

    @Override
    public InventoryDto getInventoryByIngredientName(String itemName) {
        log.debug("Fetching inventory with item name: {}", itemName);

        Inventory inventory = inventoryRepository.findByItemName(itemName)
                .orElseThrow(() -> {
                    log.warn("Inventory not found with item name: {}", itemName);
                    return new ResourceNotFoundException("Inventory not found with item name: " + itemName);
                });

        InventoryDto inventoryDto = inventoryMapper.toDto(inventory);
        log.info("Fetched inventory: {}", inventoryDto);
        return inventoryDto;
    }

    @Override
    public InventoryDto createInventory(InventoryCreateDto inventoryCreateDto) {
        String itemName = inventoryCreateDto.getItemName();
        log.debug("Attempting to create inventory with item name: {}", itemName);

        if (inventoryRepository.findByItemName(itemName).isPresent()) {
            log.warn("Inventory already exists with item name: {}", itemName);
            throw new BadRequestException("Inventory already exists with item name: " + itemName);
        }

        Inventory inventory = inventoryMapper.toEntity(inventoryCreateDto);
        Inventory savedInventory = inventoryRepository.save(inventory);
        InventoryDto inventoryDto = inventoryMapper.toDto(savedInventory);

        log.info("Inventory created successfully with ID: {}", savedInventory.getId());
        return inventoryDto;
    }

    @Override
    public String deleteInventory(String inventoryId) {
        UUID parsedId = UUIDUtils.parseUUID(inventoryId);
        log.debug("Attempting to delete inventory with ID: {}", parsedId);

        Inventory inventory = inventoryRepository.findById(parsedId)
                .orElseThrow(() -> {
                    log.warn("Inventory not found with ID: {}", inventoryId);
                    return new ResourceNotFoundException("Inventory not found with ID: " + inventoryId);
                });

        inventoryRepository.delete(inventory);
        log.info("Inventory deleted successfully with ID: {}", inventoryId);
        return "Inventory deleted successfully.";
    }

    @Override
    public InventoryDto updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto) {
        UUID parsedId = UUIDUtils.parseUUID(inventoryId);
        log.debug("Attempting to update inventory with ID: {}", parsedId);

        Inventory inventory = inventoryRepository.findById(parsedId)
                .orElseThrow(() -> {
                    log.warn("Inventory not found with ID: {}", inventoryId);
                    return new ResourceNotFoundException("Inventory not found with ID: " + inventoryId);
                });

        inventory.setItemName(inventoryCreateDto.getItemName());
        inventory.setQuantity(inventoryCreateDto.getQuantity());
        inventory.setUnit(inventoryCreateDto.getUnit());
        inventory.setLowStock(false);

        InventoryDto inventoryDto = inventoryMapper.toDto(inventoryRepository.save(inventory));

        log.info("Inventory updated successfully with ID: {}", inventoryId);
        return inventoryDto;
    }
}
