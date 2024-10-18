package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.InventoryMapper;
import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.model.dto.inventory.InventoryDto;
import com.mitar.dipl.model.entity.Inventory;
import com.mitar.dipl.repository.InventoryRepository;
import com.mitar.dipl.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public ResponseEntity<?> getInventories() {
        return ResponseEntity.ok(inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDto)
                .toList());
    }

    @Override
    public ResponseEntity<?> getInventoryById(String inventoryId) {
        UUID parsedId;
        try {
            parsedId = UUID.fromString(inventoryId);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for Inventory ID: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Inventory ID format.");
        }

        Optional<Inventory> inventoryOpt = inventoryRepository.findById(parsedId);
        if (inventoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found.");
        }

        return ResponseEntity.ok(inventoryMapper.toDto(inventoryOpt.get()));
    }

    @Override
    public ResponseEntity<?> getInventoryByIngredientName(String itemName) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByItemName(itemName);
        if (inventoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found.");
        }

        return ResponseEntity.ok(inventoryMapper.toDto(inventoryOpt.get()));
    }

    @Override
    public ResponseEntity<?> createInventory(InventoryCreateDto inventoryCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                inventoryMapper.toDto(inventoryRepository.save(inventoryMapper.toEntity(inventoryCreateDto))));
    }

    @Override
    public ResponseEntity<?> deleteInventory(String inventoryId) {
        UUID parsedId;
        try {
            parsedId = UUID.fromString(inventoryId);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for Inventory deletion: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Inventory ID format.");
        }

        Optional<Inventory> inventoryOpt = inventoryRepository.findById(parsedId);
        if (inventoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found.");
        }

        inventoryRepository.delete(inventoryOpt.get());
        return ResponseEntity.ok("Inventory deleted successfully.");
    }

    @Override
    public ResponseEntity<?> updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto) {
        UUID parsedId;
        try {
            parsedId = UUID.fromString(inventoryId);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for Inventory update: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Inventory ID format.");
        }

        Optional<Inventory> optionalInventory = inventoryRepository.findById(parsedId);
        if (optionalInventory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found.");
        }

        Inventory inventory = optionalInventory.get();

        inventory.setItemName(inventoryCreateDto.getItemName());
        inventory.setQuantity(inventoryCreateDto.getQuantity());
        inventory.setUnit(inventoryCreateDto.getUnit());

        return ResponseEntity.ok(inventoryMapper.toDto(inventoryRepository.save(inventory)));
    }
}
