package com.mitar.dipl.service.implementation;


import com.mitar.dipl.mapper.TransactionMapper;
import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.Transaction;
import com.mitar.dipl.repository.BillRepository;
import com.mitar.dipl.repository.TransactionRepository;
import com.mitar.dipl.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private BillRepository billRepository;

    private TransactionMapper transactionMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Override
    public ResponseEntity<?> getAllTransactions() {
        logger.info("Fetching all transactions.");
        return ResponseEntity.status(HttpStatus.OK).body(
                transactionRepository.findAll().stream()
                        .map(transactionMapper::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<?> getTransactionById(String transactionId) {
        UUID transactionUUID = UUIDUtils.parseUUID(transactionId);
        Optional<Transaction> transaction = transactionRepository.findById(transactionUUID);
        if (transaction.isEmpty()) {
            logger.warn("Transaction not found with ID: {}", transactionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionMapper.toDto(transaction.get()));
    }

    @Override
    public ResponseEntity<?> getTransactionsByBillId(String billId) {
        UUID billUUID = UUIDUtils.parseUUID(billId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionRepository.findAllByBill_Id(billUUID).stream()
                .map(transactionMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> createTransaction(TransactionCreateDto transactionCreateDto) {
        UUID billUUID = UUIDUtils.parseUUID(transactionCreateDto.getBillId());
        Optional<Bill> bill = billRepository.findById(billUUID);
        if (bill.isEmpty()) {
            logger.warn("Bill not found with ID: {}", transactionCreateDto.getBillId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill not found.");
        }
        Bill billEntity = bill.get();
        logger.info("Creating transaction for bill with ID: {}", transactionCreateDto);
        logger.info("Checking if bill has sufficient funds. {} < {}", billEntity.getFinalAmount(), transactionCreateDto.getAmount());
        if (billEntity.getFinalAmount().compareTo(transactionCreateDto.getAmount()) > 0) {
            logger.warn("Insufficient funds in bill with ID: {}", transactionCreateDto.getBillId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient funds.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionRepository.save(transactionMapper.toEntity(transactionCreateDto, billEntity)));
    }

}
