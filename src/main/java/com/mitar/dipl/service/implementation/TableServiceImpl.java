package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.ConflictException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.TableMapper;
import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.dto.table_entity.TableDto;
import com.mitar.dipl.model.entity.Reservation;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.ReservationRepository;
import com.mitar.dipl.repository.TableRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.TableService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final TableMapper tableMapper;


    @Override
    public List<TableDto> getAllTables() {
        log.info("Fetching all tables.");
        List<TableDto> tableDtos = tableRepository.findAll().stream()
                .map(tableMapper::toDto)
                .toList();

        log.info("Fetched {} tables.", tableDtos.size());
        return tableDtos;
    }

    @Override
    public TableDto getTableById(String tableId) {
        UUID parsedTableId = UUIDUtils.parseUUID(tableId);
        log.debug("Fetching Table with ID: {}", parsedTableId);

        TableEntity table = tableRepository.findById(parsedTableId)
                .orElseThrow(() -> {
                    log.warn("Table not found with ID: {}", tableId);
                    return new ResourceNotFoundException("Table not found with ID: " + tableId);
                });

        TableDto tableDto = tableMapper.toDto(table);
        log.info("Retrieved Table ID: {}", tableId);
        return tableDto;
    }

    @Override
    public TableDto getTableByTableNumber(Integer tableNumber) {
        log.debug("Fetching Table with table number: {}", tableNumber);

        TableEntity table = tableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> {
                    log.warn("Table not found with table number: {}", tableNumber);
                    return new ResourceNotFoundException("Table not found with table number: " + tableNumber);
                });

        TableDto tableDto = tableMapper.toDto(table);
        log.info("Retrieved Table with table number: {}", tableNumber);
        return tableDto;
    }

    @Override
    public TableDto createTable(TableCreateDto tableCreateDto) {
        log.info("Attempting to create table with table number: {}", tableCreateDto.getTableNumber());

        if (tableRepository.findByTableNumber(tableCreateDto.getTableNumber()).isPresent()) {
            log.warn("Table with table number {} already exists.", tableCreateDto.getTableNumber());
            throw new ConflictException("Table with table number " + tableCreateDto.getTableNumber() + " already exists.");
        }

        TableEntity tableEntity = tableMapper.toEntity(tableCreateDto);

        TableEntity savedTable = tableRepository.save(tableEntity);
        log.info("Table created successfully with ID: {}", savedTable.getId());

        return tableMapper.toDto(savedTable);
    }

    @Override
    public String deleteTable(String tableId) {
        UUID uuid = UUIDUtils.parseUUID(tableId);
        log.debug("Attempting to delete Table with ID: {}", uuid);

        TableEntity table = tableRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.warn("Table not found with ID: {}", tableId);
                    return new ResourceNotFoundException("Table not found with ID: " + tableId);
                });

        List<Reservation> reservations = reservationRepository.findAllByTable_Id(uuid);
        for (Reservation reservation : reservations) {
            reservation.setTable(null);
            reservation.setDeleted(true);
            reservationRepository.save(reservation);
            log.debug("Soft-deleted Reservation ID: {} associated with Table ID: {}", reservation.getId(), tableId);
        }

        tableRepository.delete(table);
        log.info("Table deleted successfully with ID: {}", tableId);
        return "Table deleted successfully.";
    }

    @Override
    public TableDto updateTable(String tableId, TableCreateDto tableCreateDto) {
        UUID uuid = UUIDUtils.parseUUID(tableId);
        log.debug("Attempting to update Table with ID: {}", uuid);

        TableEntity existingTable = tableRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.warn("Table not found with ID: {}", tableId);
                    return new ResourceNotFoundException("Table not found with ID: " + tableId);
                });

        if (!existingTable.getTableNumber().equals(tableCreateDto.getTableNumber())) {
            if (tableRepository.findByTableNumber(tableCreateDto.getTableNumber()).isPresent()) {
                log.warn("Table number {} is already in use.", tableCreateDto.getTableNumber());
                throw new ConflictException("Table number " + tableCreateDto.getTableNumber() + " is already in use.");
            }
        }

        existingTable.setTableNumber(tableCreateDto.getTableNumber());
        existingTable.setCapacity(tableCreateDto.getCapacity());
        existingTable.setIsAvailable(tableCreateDto.getIsAvailable());

        TableEntity updatedTable = tableRepository.save(existingTable);
        log.info("Table updated successfully with ID: {}", tableId);

        return tableMapper.toDto(updatedTable);
    }
}
