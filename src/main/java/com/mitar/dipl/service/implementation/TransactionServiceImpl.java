package com.mitar.dipl.service.implementation;


import com.mitar.dipl.mapper.TransactionMapper;
import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.entity.Transaction;
import com.mitar.dipl.repository.TransactionRepository;
import com.mitar.dipl.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    private TransactionMapper transactionMapper;


    @Override
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.status(HttpStatus.OK).body(transactionRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getTransactionById(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(UUID.fromString(transactionId));
        if (transaction.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(transaction.get());
    }

    @Override
    public ResponseEntity<?> getTransactionsByBillId(String billId) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionRepository.findAllByBill_Id(UUID.fromString(billId)));
    }

    @Override
    public ResponseEntity<?> createTransaction(TransactionCreateDto transactionCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionRepository.save(transactionMapper.toEntity(transactionCreateDto)));
    }

    @Override
    public ResponseEntity<?> deleteTransaction(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(UUID.fromString(transactionId));
        if (transaction.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        transactionRepository.delete(transaction.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
