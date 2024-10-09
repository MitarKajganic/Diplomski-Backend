package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import org.springframework.http.ResponseEntity;

public interface TableService {

    ResponseEntity<?> getAllTables();

    ResponseEntity<?> getTableById(String tableId);

    ResponseEntity<?> getTableByTableNumber(Integer tableNumber);

    ResponseEntity<?> createTable(TableCreateDto tableCreateDto);

    ResponseEntity<?> deleteTable(String tableId);

    ResponseEntity<?> updateTable(String tableId, TableCreateDto tableCreateDto);

}
