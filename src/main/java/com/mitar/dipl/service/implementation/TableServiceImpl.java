package com.mitar.dipl.service.implementation;


import com.mitar.dipl.mapper.TableMapper;
import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.repository.TableRepository;
import com.mitar.dipl.service.TableService;
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
public class TableServiceImpl implements TableService {

    private TableRepository tableRepository;

    private TableMapper tableMapper;


    @Override
    public ResponseEntity<?> getAllTables() {
        return ResponseEntity.status(HttpStatus.OK).body(tableRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getTableById(String tableId) {
        Optional<TableEntity> table = tableRepository.findById(UUID.fromString(tableId));
        if (table.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(table.get());
    }

    @Override
    public ResponseEntity<?> getTableByTableNumber(Integer tableNumber) {
        Optional<TableEntity> table = tableRepository.findByTableNumber(tableNumber);
        if (table.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(table.get());
    }

    @Override
    public ResponseEntity<?> createTable(TableCreateDto tableCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableRepository.save(tableMapper.toEntity(tableCreateDto)));
    }

    @Override
    public ResponseEntity<?> deleteTable(String tableId) {
        Optional<TableEntity> table = tableRepository.findById(UUID.fromString(tableId));
        if (table.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        tableRepository.delete(table.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> updateTable(String tableId, TableCreateDto tableCreateDto) {
        Optional<TableEntity> table = tableRepository.findById(UUID.fromString(tableId));
        if (table.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        TableEntity tableEntity = table.get();

        tableEntity.setTableNumber(tableCreateDto.getTableNumber());
        tableEntity.setCapacity(tableCreateDto.getCapacity());
        tableEntity.setIsAvailable(tableCreateDto.getIsAvailable());

        return ResponseEntity.status(HttpStatus.OK).body(tableRepository.save(tableEntity));
    }
}
