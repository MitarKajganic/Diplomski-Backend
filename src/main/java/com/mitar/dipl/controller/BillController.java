package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.service.BillService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;

    @GetMapping("/{billId}")
    public ResponseEntity<?> getBillById(@PathVariable String billId) {
        return billService.getBillById(billId);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getBillByOrderId(@PathVariable String orderId) {
        return billService.getBillByOrderId(orderId);
    }

    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody @Validated BillCreateDto billCreateDto) {
        return billService.createBill(billCreateDto);
    }

    @DeleteMapping("/delete/{billId}")
    public ResponseEntity<?> deleteBill(@PathVariable String billId) {
        return billService.deleteBill(billId);
    }

    @PutMapping("/update/{billId}")
    public ResponseEntity<?> updateBill(@PathVariable String billId, @RequestBody @Validated BillCreateDto billCreateDto) {
        return billService.updateBill(billId, billCreateDto);
    }

}
