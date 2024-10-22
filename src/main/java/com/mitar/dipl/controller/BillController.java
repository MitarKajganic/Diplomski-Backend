package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.service.BillService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;

    @GetMapping
    public ResponseEntity<?> getAllBills() {
        return ResponseEntity.status(HttpStatus.OK).body(billService.getAll());
    }

    @GetMapping("/{billId}")
    public ResponseEntity<?> getBillById(@PathVariable String billId) {
        return ResponseEntity.status(HttpStatus.OK).body(billService.getBillById(billId));
    }

    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody @Validated BillCreateDto billCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billService.createBill(billCreateDto));
    }

}
