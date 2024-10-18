package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.dto.transaction.TransactionDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.Transaction;
import com.mitar.dipl.model.entity.enums.Type;
import com.mitar.dipl.repository.BillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TransactionMapper {

    private BillRepository billRepository;

    public TransactionDto toDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId().toString());
        transactionDto.setTransactionTime(transaction.getTransactionTime());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setType(transaction.getType().name());
        transactionDto.setBillId(transaction.getBill().getId().toString());
        return transactionDto;
    }

    public Transaction toEntity(TransactionCreateDto transactionCreateDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionCreateDto.getAmount());
        transaction.setType(Type.valueOf(transactionCreateDto.getType()));

        Optional<Bill> bill = billRepository.findById(UUID.fromString(transactionCreateDto.getBillId()));
        bill.ifPresent(transaction::setBill);

        return transaction;
    }

}
