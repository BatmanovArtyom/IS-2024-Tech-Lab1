package com.tech.labs.Exceptions;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionException extends Exception {
    public TransactionException(String message) {
        super(message);
    }

    public static TransactionException negativeAmount() {
        return new TransactionException("Amount is < 0 or <= 0");
    }

    public static TransactionException failedTransaction(String message) {
        return new TransactionException("Transaction failed\n" + message);
    }

    public static TransactionException transactionAlreadyExists(UUID transactionId) {
        return new TransactionException("Transaction with id: " + transactionId.toString() + " already exists");
    }

    public static TransactionException transactionDoesNotExist(UUID id) {
        return new TransactionException("Transaction with id: " + id.toString() + " doesn't exist");
    }

    public static TransactionException sumExceedingLimit(BigDecimal sum, long limit) {
        return new TransactionException("Sum " + sum + " exceeding the limit " + limit);
    }
}
