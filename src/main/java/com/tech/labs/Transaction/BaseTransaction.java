package com.tech.labs.Transaction;

import com.tech.labs.Accounts.Commands.BalanceOperationCommand;

public class BaseTransaction extends BankTransaction {
    public BaseTransaction(BalanceOperationCommand command) {
        super(command);
    }
}
