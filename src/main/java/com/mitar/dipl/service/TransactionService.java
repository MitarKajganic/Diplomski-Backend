package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.mitar.dipl.model.dto.transaction.TransactionDto;
import com.stripe.model.checkout.Session;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    /**
     * Fetches all transactions.
     *
     * @return List of TransactionDto
     */
    List<TransactionDto> getAllTransactions();

    /**
     * Fetches a transaction by its ID.
     *
     * @param transactionId The UUID of the transaction as a string.
     * @return TransactionDto
     */
    TransactionDto getTransactionById(String transactionId);

    /**
     * Fetches a transaction by its Stripe Session ID.
     *
     * @param stripeSessionId The Stripe Session ID.
     * @return TransactionDto
     */
    TransactionDto getByStripeSessionId(String stripeSessionId);

    /**
     * Fetches transactions by Bill ID.
     *
     * @param billId The UUID of the bill as a string.
     * @return List of TransactionDto
     */
    List<TransactionDto> getTransactionsByBillId(String billId);

    /**
     * Creates a new transaction.
     *
     * @param transactionCreateDto The DTO containing transaction creation data.
     * @return TransactionDto
     */
    TransactionDto createTransaction(TransactionCreateDto transactionCreateDto);

    /**
     * Completes a transaction based on Stripe webhook.
     *
     * @param uuid The UUID of the transaction.
     * @param session The Stripe Checkout Session.
     */
    void completeTransaction(UUID uuid, Session session);

    /**
     * Fails a transaction based on Stripe webhook.
     *
     * @param uuid The UUID of the transaction.
     * @param session The Stripe Checkout Session.
     */
    void failTransaction(UUID uuid, Session session);

}
