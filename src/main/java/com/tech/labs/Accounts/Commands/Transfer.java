package com.tech.labs.Accounts.Commands;

import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Accounts.BaseAccount;

import java.math.BigDecimal;

public class Transfer implements BalanceOperationCommand {
    private final BaseAccount toAccount;
    private final BaseAccount fromAccount;
    private final Integer sum;

    public Transfer(BaseAccount toAccount, BaseAccount fromAccount, Integer sum) throws TransactionException {
        if (sum < 0) {
            throw TransactionException.negativeAmount();
        }
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.sum = sum;
    }

    /**
     * Executes the bank transaction by decreasing the amount from the "fromAccount"
     * and increasing the amount in the "toAccount".
     *
     * @throws TransactionException if an error occurs during the transaction,
     *                              such as insufficient funds in the "fromAccount".
     */
    @Override
    public void execute() throws TransactionException {
        try {
            fromAccount.decreaseAmount(sum);
        } catch (Exception e) {
            fromAccount.increaseAmount(sum);
            throw TransactionException.failedTransaction("Couldn't withdraw money from 1 account");
        }

        toAccount.increaseAmount(sum);
    }

    @Override
    public void cancel() throws TransactionException {
        try {
            toAccount.decreaseAmount(sum);
        } catch (Exception e) {
            toAccount.increaseAmount(sum);
            throw TransactionException.failedTransaction("Couldn't withdraw money from 2 account");
        }

        fromAccount.increaseAmount(sum);
    }
}
