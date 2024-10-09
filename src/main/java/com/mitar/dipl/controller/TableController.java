package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.service.TableService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTables() {
        return tableService.getAllTables();
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<?> getTableById(@PathVariable String tableId) {
        return tableService.getTableById(tableId);
    }

    @GetMapping("/table-number/{tableNumber}")
    public ResponseEntity<?> getTableByTableNumber(@PathVariable Integer tableNumber) {
        return tableService.getTableByTableNumber(tableNumber);
    }

    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody @Validated TableCreateDto tableCreateDto) {
        return tableService.createTable(tableCreateDto);
    }

    @DeleteMapping("/delete/{tableId}")
    public ResponseEntity<?> deleteTable(@PathVariable String tableId) {
        return tableService.deleteTable(tableId);
    }

    @PutMapping("/update/{tableId}")
    public ResponseEntity<?> updateTable(@PathVariable String tableId, @RequestBody @Validated TableCreateDto tableCreateDto) {
        return tableService.updateTable(tableId, tableCreateDto);
    }

}
