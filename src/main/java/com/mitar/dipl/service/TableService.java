package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.dto.table_entity.TableDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TableService {

    /**
     * Fetches all tables.
     *
     * @return List of TableDto
     */
    List<TableDto> getAllTables();

    /**
     * Fetches a table by its ID.
     *
     * @param tableId The UUID of the table as a string.
     * @return TableDto
     */
    TableDto getTableById(String tableId);

    /**
     * Fetches a table by its table number.
     *
     * @param tableNumber The table number.
     * @return TableDto
     */
    TableDto getTableByTableNumber(Integer tableNumber);

    /**
     * Creates a new table.
     *
     * @param tableCreateDto The DTO containing table creation data.
     * @return TableDto
     */
    TableDto createTable(TableCreateDto tableCreateDto);

    /**
     * Deletes a table by its ID.
     *
     * @param tableId The UUID of the table as a string.
     * @return Success message.
     */
    String deleteTable(String tableId);

    /**
     * Updates an existing table.
     *
     * @param tableId        The UUID of the table as a string.
     * @param tableCreateDto The DTO containing updated table data.
     * @return TableDto
     */
    TableDto updateTable(String tableId, TableCreateDto tableCreateDto);

}
