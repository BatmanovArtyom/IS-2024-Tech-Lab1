package com.tech.labs.Accounts.Commands;

import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Exceptions.TransactionException;

public interface BalanceOperationCommand extends Command {
    void cancel() throws TransactionException, AccountException;
}
