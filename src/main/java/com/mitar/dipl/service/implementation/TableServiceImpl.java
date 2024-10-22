package com.mitar.dipl.service.implementation;


import com.mitar.dipl.mapper.TableMapper;
import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.repository.ReservationRepository;
import com.mitar.dipl.repository.TableRepository;
import com.mitar.dipl.service.TableService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TableServiceImpl.class);
    private ReservationRepository reservationRepository;


    @Override
    public ResponseEntity<?> getAllTables() {
        logger.info("Fetching all tables.");
        return ResponseEntity.status(HttpStatus.OK).body(tableRepository.findAll().stream()
                .map(tableMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getTableById(String tableId) {
        UUID tableUUID = UUIDUtils.parseUUID(tableId);
        Optional<TableEntity> table = tableRepository.findById(tableUUID);
        if (table.isEmpty()) {
            logger.warn("Table not found with ID: {}", tableId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tableMapper.toDto(table.get()));
    }

    @Override
    public ResponseEntity<?> getTableByTableNumber(Integer tableNumber) {
        Optional<TableEntity> table = tableRepository.findByTableNumber(tableNumber);
        if (table.isEmpty()) {
            logger.warn("Table not found with table number: {}", tableNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tableMapper.toDto(table.get()));
    }

    @Override
    public ResponseEntity<?> createTable(TableCreateDto tableCreateDto) {
        Optional<TableEntity> table = tableRepository.findByTableNumber(tableCreateDto.getTableNumber());
        if (table.isPresent()) {
            logger.warn("Table with table number {} already exists.", tableCreateDto.getTableNumber());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Table with table number " + tableCreateDto.getTableNumber() + " already exists.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(tableMapper.toDto(tableRepository.save(tableMapper.toEntity(tableCreateDto))));
    }

    @Override
    public ResponseEntity<?> deleteTable(String tableId) {
        Optional<TableEntity> table = tableRepository.findById(UUID.fromString(tableId));
        if (table.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        TableEntity tableEntity = table.get();
        tableEntity.getReservations().forEach(reservation -> {
            reservation.setTable(null);
            reservation.setDeleted(true);
            reservationRepository.save(reservation);
        });

        tableRepository.delete(tableEntity);
        return ResponseEntity.status(HttpStatus.OK).body("Table deleted successfully.");
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
