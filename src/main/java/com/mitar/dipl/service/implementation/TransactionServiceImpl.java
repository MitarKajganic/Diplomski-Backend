package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.TransactionMapper;
import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.dto.transaction.TransactionDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.Transaction;
import com.mitar.dipl.model.entity.enums.Method;
import com.mitar.dipl.model.entity.enums.Status;
import com.mitar.dipl.repository.BillRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.repository.TransactionRepository;
import com.mitar.dipl.service.TransactionService;
import com.mitar.dipl.utils.StripeService;
import com.mitar.dipl.utils.UUIDUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BillRepository billRepository;
    private final TransactionMapper transactionMapper;
    private final OrderRepository orderRepository;
    private final StripeService stripeService;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<TransactionDto> getAllTransactions() {
        log.info("Fetching all transactions.");
        List<TransactionDto> transactionDtos = transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .toList();
        log.info("Fetched {} transactions.", transactionDtos.size());
        return transactionDtos;
    }

    @Override
    public TransactionDto getTransactionById(String transactionId) {
        UUID parsedTransactionId = UUIDUtils.parseUUID(transactionId);
        log.debug("Fetching Transaction with ID: {}", parsedTransactionId);

        Transaction transaction = transactionRepository.findById(parsedTransactionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found with ID: {}", transactionId);
                    return new ResourceNotFoundException("Transaction not found with ID: " + transactionId);
                });

        TransactionDto transactionDto = transactionMapper.toDto(transaction);
        log.info("Retrieved Transaction ID: {}", transactionId);
        return transactionDto;
    }

    @Override
    public TransactionDto getByStripeSessionId(String stripeSessionId) {
        log.debug("Fetching Transaction by Stripe Session ID: {}", stripeSessionId);

        Transaction transaction = transactionRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found with Stripe Session ID: {}", stripeSessionId);
                    return new ResourceNotFoundException("Transaction not found with Stripe Session ID: " + stripeSessionId);
                });

        TransactionDto transactionDto = transactionMapper.toDto(transaction);
        log.info("Retrieved Transaction ID: {}", transaction.getId());
        return transactionDto;
    }

    @Override
    public List<TransactionDto> getTransactionsByBillId(String billId) {
        UUID parsedBillId = UUIDUtils.parseUUID(billId);
        log.debug("Fetching Transactions for Bill ID: {}", parsedBillId);

        List<TransactionDto> transactionDtos = transactionRepository.findAllByBill_Id(parsedBillId).stream()
                .map(transactionMapper::toDto)
                .toList();

        log.info("Fetched {} transactions for Bill ID: {}", transactionDtos.size(), billId);
        return transactionDtos;
    }

    @Override
    public TransactionDto createTransaction(TransactionCreateDto transactionCreateDto) {
        UUID billUUID = UUIDUtils.parseUUID(transactionCreateDto.getBillId());
        log.info("Creating transaction for Bill ID: {}", transactionCreateDto.getBillId());

        Bill bill = billRepository.findById(billUUID)
                .orElseThrow(() -> {
                    log.warn("Bill not found with ID: {}", transactionCreateDto.getBillId());
                    return new ResourceNotFoundException("Bill not found with ID: " + transactionCreateDto.getBillId());
                });

        if (Objects.equals(transactionCreateDto.getMethod(), Method.CARD.toString())) {
            return handleCardTransaction(transactionCreateDto, bill);
        } else if (Objects.equals(transactionCreateDto.getMethod(), Method.CASH.toString())) {
            return handleCashTransaction(transactionCreateDto, bill);
        } else {
            throw new BadRequestException("Unsupported transaction type.");
        }
    }

    private TransactionDto handleCardTransaction(TransactionCreateDto transactionCreateDto, Bill bill) {
        try {
            // Initialize transaction as PENDING
            Transaction transaction = transactionMapper.toEntity(transactionCreateDto, bill);
            transaction.setStripeStatus("PENDING");
            Transaction savedTransaction = transactionRepository.save(transaction);

            OrderEntity order = orderRepository.findByBill_Id(bill.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found for Bill ID: " + bill.getId()));

            // Create Stripe Checkout Session
            Session session = stripeService.createCheckoutSession(transactionCreateDto, savedTransaction.getId().toString(), bill.getId().toString(), order.getId().toString());
            savedTransaction.setStripeSessionId(session.getId());
            savedTransaction.setStripeUrl(session.getUrl()); // Set the stripeUrl
            transactionRepository.save(savedTransaction);

            log.info("Stripe Checkout Session created with ID: {}", session.getId());

            // Return the session URL for frontend redirection
            TransactionDto transactionDto = transactionMapper.toDto(savedTransaction);
            transactionDto.setStripeUrl(session.getUrl()); // Ensure stripeUrl is included
            return transactionDto;

        } catch (StripeException e) {
            log.error("Stripe exception occurred: {}", e.getMessage());
            throw new BadRequestException("Failed to create Stripe Checkout Session.");
        }
    }

    private TransactionDto handleCashTransaction(TransactionCreateDto transactionCreateDto, Bill bill) {
        log.debug("Checking if Bill has sufficient funds. Available: {}, Required: {}",
                bill.getFinalAmount(), transactionCreateDto.getAmount());

        if (bill.getFinalAmount().setScale(2, RoundingMode.HALF_UP)
                .compareTo(transactionCreateDto.getAmount().setScale(2, RoundingMode.HALF_UP)) < 0) {
            log.warn("Insufficient funds in Bill ID: {}. Available: {}, Required: {}",
                    transactionCreateDto.getBillId(), bill.getFinalAmount(), transactionCreateDto.getAmount());
            throw new BadRequestException("Insufficient funds.");
        }

        // Deduct the amount
        bill.calculateFinalAmount();
        billRepository.save(bill);
        log.debug("Deducted {} from Bill ID: {}. New balance: {}",
                transactionCreateDto.getAmount(), transactionCreateDto.getBillId(), bill.getFinalAmount());

        // Save the transaction as COMPLETED
        Transaction savedTransaction = transactionRepository.save(transactionMapper.toEntity(transactionCreateDto, bill));
        entityManager.flush();
        entityManager.refresh(savedTransaction);
        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());

        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    public void completeTransaction(UUID transactionId, Session session) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        if ("PENDING".equals(transaction.getStripeStatus())) {
            Bill bill = transaction.getBill();

            // Check if the bill has sufficient funds
            if (bill.getFinalAmount().setScale(2, RoundingMode.HALF_UP)
                    .compareTo(transaction.getAmount().setScale(2, RoundingMode.HALF_UP)) < 0) {
                log.warn("Insufficient funds for Transaction ID: {}", transactionId);
                transaction.setStripeStatus("FAILED");
                transactionRepository.save(transaction);
                // Optionally, notify the user about the failure
                return;
            }

            // Deduct the amount
//            bill.setFinalAmount(bill.getFinalAmount().subtract(transaction.getAmount()));
//            billRepository.save(bill);

            // Update transaction status
            transaction.setStripeStatus("COMPLETED");
            transactionRepository.save(transaction);

            log.info("Transaction ID: {} completed successfully.", transactionId);
        } else {
            log.warn("Transaction ID: {} is not in PENDING state.", transactionId);
        }
    }

    @Override
    public void failTransaction(UUID transactionId, Session session) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        if ("PENDING".equals(transaction.getStripeStatus())) {
            transaction.setStripeStatus("FAILED");
            transactionRepository.save(transaction);

            OrderEntity order = orderRepository.findByBill_Id(transaction.getBill().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found for Transaction ID: " + transactionId));

            order.setStatus(Status.CANCELLED);
            orderRepository.save(order);

            log.info("Transaction ID: {} failed.", transactionId);
        } else {
            log.warn("Transaction ID: {} is not in PENDING state.", transactionId);
        }
    }

}
