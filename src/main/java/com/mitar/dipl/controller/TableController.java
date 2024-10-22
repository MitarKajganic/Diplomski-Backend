package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.service.TableService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getAllTables());
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<?> getTableById(@PathVariable String tableId) {
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getTableById(tableId));
    }

    @GetMapping("/table-number/{tableNumber}")
    public ResponseEntity<?> getTableByTableNumber(@PathVariable @Min(value = 1, message = "Table number must be greater than zero") Integer tableNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getTableByTableNumber(tableNumber));
    }

    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody @Validated TableCreateDto tableCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(tableCreateDto));
    }

    @DeleteMapping("/delete/{tableId}")
    public ResponseEntity<?> deleteTable(@PathVariable String tableId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(tableService.deleteTable(tableId));
    }

    @PutMapping("/update/{tableId}")
    public ResponseEntity<?> updateTable(@PathVariable String tableId, @RequestBody @Validated TableCreateDto tableCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(tableService.updateTable(tableId, tableCreateDto));
    }

}
