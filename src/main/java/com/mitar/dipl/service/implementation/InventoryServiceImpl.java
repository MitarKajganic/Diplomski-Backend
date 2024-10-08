package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.InventoryMapper;
import com.mitar.dipl.model.dto.inventory.InventoryCreateDto;
import com.mitar.dipl.model.entity.Inventory;
import com.mitar.dipl.repository.InventoryRepository;
import com.mitar.dipl.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;

    private InventoryMapper inventoryMapper;

    @Override
    public ResponseEntity<?> getInventoryById(String inventoryId) {
        Optional<Inventory> inventory = inventoryRepository.findById(UUID.fromString(inventoryId));
        if (inventory.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(inventoryMapper.toDto(inventory.get()));
    }

    @Override
    public ResponseEntity<?> getInventoryByIngredientName(String ingredientName) {
        Optional<Inventory> inventory = inventoryRepository.findByIngredientName(ingredientName);
        if (inventory.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(inventoryMapper.toDto(inventory.get()));
    }

    @Override
    public ResponseEntity<?> deleteInventory(String inventoryId) {
        Optional<Inventory> inventory = inventoryRepository.findById(UUID.fromString(inventoryId));
        if (inventory.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        inventoryRepository.delete(inventory.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> createInventory(InventoryCreateDto inventoryCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toDto(inventoryRepository.save(inventoryMapper.toEntity(inventoryCreateDto))));
    }

    @Override
    public ResponseEntity<?> updateInventory(String inventoryId, InventoryCreateDto inventoryCreateDto) {
        Optional<Inventory> inventory = inventoryRepository.findById(UUID.fromString(inventoryId));
        if (inventory.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        inventoryRepository.save(inventoryMapper.toEntity(inventoryCreateDto));
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
