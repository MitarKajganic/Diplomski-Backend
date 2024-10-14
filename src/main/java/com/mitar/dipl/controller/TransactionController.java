package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getTransactionsByBillId(@PathVariable String billId) {
        return transactionService.getTransactionsByBillId(billId);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody @Validated TransactionCreateDto transactionCreateDto) {
        return transactionService.createTransaction(transactionCreateDto);
    }

    @DeleteMapping("/delete/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String transactionId) {
        return transactionService.deleteTransaction(transactionId);
    }
}
