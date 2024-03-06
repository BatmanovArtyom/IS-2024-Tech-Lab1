package com.tech.labs.Accounts;

import com.tech.labs.Exceptions.AccountException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Interfaces.Clients;
import com.tech.labs.Transaction.BankTransaction;

public abstract class BaseAccount {
    private final List<BankTransaction> transactions = new ArrayList<>();

    @Getter
    private final TypeOfBankAccount type;

    @Getter
    private final UUID id;

    @Getter
    private final Clients client;

    @Getter
    @Setter
    private Integer balance;

    public BaseAccount(Clients client, TypeOfBankAccount type) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        this.type = type;
        this.id = UUID.randomUUID();
        this.client = client;
    }

    public BankTransaction getTransaction(UUID transactionId) throws TransactionException {
        return transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> TransactionException.transactionDoesNotExist(transactionId));
    }
    public void increaseAmount(Integer sum) throws TransactionException {
        if (sum <= 0) {
            throw TransactionException.negativeAmount();
        }
        balance += sum;
    }

    public void saveChanges(BankTransaction transaction) throws TransactionException {
        if (transactions.contains(transaction)) {
            throw TransactionException.transactionAlreadyExists(transaction.getId());
        }
        transactions.add(transaction);
    }

    public void decreaseAmount(Integer sum) throws TransactionException, AccountException {
    }
}
