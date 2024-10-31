package com.mitar.dipl.utils;

import com.mitar.dipl.model.dto.transaction.TransactionCreateDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String apiKey;

    @Value("${stripe.redirect.url}")
    private String redirectUrl; // Single redirect URL for both success and cancel

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public Session createCheckoutSession(TransactionCreateDto transactionCreateDto, String transactionId, String billId, String orderId) throws StripeException {
        // Convert amount to cents (Stripe expects amounts in the smallest currency unit)
        long amountInCents = transactionCreateDto.getAmount().multiply(new BigDecimal("100")).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                //.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Optional: Enable card payments explicitly
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/payment-status?status=success&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/payment-status?status=cancel&session_id={CHECKOUT_SESSION_ID}")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Transaction Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("transaction_id", transactionId)
                .putMetadata("order_id", orderId)
                .putMetadata("bill_id", billId)
                .build();

        return Session.create(params);
    }

    public Session retrieveCheckoutSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}
