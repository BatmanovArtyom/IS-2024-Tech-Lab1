package com.tech.labs.Transaction;

import com.tech.labs.Accounts.Commands.BalanceOperationCommand;
import com.tech.labs.Exceptions.TransactionException;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class BankTransaction {
    private final BalanceOperationCommand command;

    @Getter
    private final UUID id = UUID.randomUUID();

    @Getter
    @Setter
    private State transactionState = State.Started;

    @Getter
    @Setter
    private String statusMessage = "BankTransaction " + transactionState.toString();

    public BankTransaction(BalanceOperationCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        this.command = command;
    }

    public void doTransaction() throws TransactionException {
        if (transactionState != State.Started && transactionState != State.Canceled) {
            throw TransactionException.failedTransaction("Transaction already in progress or canceled.");
        }

        try {
            command.execute();
            transactionState = State.Ended;
            statusMessage = "BankTransaction " + transactionState.toString();
        } catch (Exception e) {
            transactionState = State.Failed;
            statusMessage = "BankTransaction " + transactionState.toString() + ": " + e.getMessage();
        }
    }

    public void undo() throws TransactionException {
        if (transactionState != State.Ended && transactionState != State.Canceled) {
            throw TransactionException.failedTransaction("Transaction not in a state to be undone.");
        }

        try {
            command.cancel();
            transactionState = State.Canceled;
            statusMessage = "BankTransaction " + transactionState.toString();
        } catch (Exception e) {
            transactionState = State.Failed;
            statusMessage = "BankTransaction " + transactionState.toString() + ": " + e.getMessage();
        }
    }

    public UUID getTransactionId() {
        return this.id;
    }
}
