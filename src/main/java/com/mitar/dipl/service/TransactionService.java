package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import org.springframework.http.ResponseEntity;

public interface TransactionService {

    ResponseEntity<?> getAllTransactions();

    ResponseEntity<?> getTransactionById(String transactionId);

    ResponseEntity<?> getTransactionsByBillId(String billId);

    ResponseEntity<?> createTransaction(TransactionCreateDto transactionCreateDto);

    ResponseEntity<?> deleteTransaction(String transactionId);

}
