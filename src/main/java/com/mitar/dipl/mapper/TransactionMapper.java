package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.dto.transaction.TransactionDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.Transaction;
import com.mitar.dipl.model.entity.enums.Method;
import com.mitar.dipl.model.entity.enums.Type;
import com.mitar.dipl.repository.BillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();

        transactionDto.setId(transaction.getId().toString());
        transactionDto.setTransactionTime(transaction.getCreatedAt());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setType(transaction.getType().name());
        transactionDto.setMethod(transaction.getMethod().name());
        transactionDto.setBillId(transaction.getBill().getId().toString());

        return transactionDto;
    }

    public Transaction toEntity(TransactionCreateDto transactionCreateDto, Bill bill) {
        Transaction transaction = new Transaction();

        transaction.setAmount(transactionCreateDto.getAmount());
        transaction.setType(Type.valueOf(transactionCreateDto.getType()));
        transaction.setMethod(Method.valueOf(transactionCreateDto.getMethod()));
        transaction.setBill(bill);

        return transaction;
    }

}
