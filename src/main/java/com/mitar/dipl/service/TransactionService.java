package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.dto.transaction.TransactionDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransactions();

    TransactionDto getTransactionById(String transactionId);

    List<TransactionDto> getTransactionsByBillId(String billId);

    TransactionDto createTransaction(TransactionCreateDto transactionCreateDto);

}
