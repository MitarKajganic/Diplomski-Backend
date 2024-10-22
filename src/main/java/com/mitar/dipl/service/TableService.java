package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.dto.table_entity.TableDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TableService {

    List<TableDto> getAllTables();

    TableDto getTableById(String tableId);

    TableDto getTableByTableNumber(Integer tableNumber);

    TableDto createTable(TableCreateDto tableCreateDto);

    String deleteTable(String tableId);

    TableDto updateTable(String tableId, TableCreateDto tableCreateDto);

}
