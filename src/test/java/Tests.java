import com.tech.labs.Accounts.AccountConfigurations.DepositPercent;
import com.tech.labs.Accounts.BaseAccount;
import com.tech.labs.Accounts.TypeOfBankAccount;
import com.tech.labs.DateTimeProvider.RewindClock;
import com.tech.labs.Entities.Bank;
import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Exceptions.BankException;
import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Interfaces.CentralBank;
import com.tech.labs.Interfaces.Clients;
import com.tech.labs.Models.Percent;
import com.tech.labs.Service.ServiceCentralBank;
import com.tech.labs.Transaction.BankTransaction;
import com.tech.labs.Transaction.State;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Tests {

    /**
     * Tests various transactions in a banking system.
     *
     * <p>This test method creates a CentralBank, registers a client, creates accounts,
     * performs transactions, and checks the results. It tests the following scenarios:
     * <ul>
     *     <li>Creation of a bank with given parameters.</li>
     *     <li>Creation of a client and registration in the central bank.</li>
     *     <li>Creation of debit accounts for the client.</li>
     *     <li>Replenishment of accounts and checking balances.</li>
     *     <li>Cancellation of transactions and verifying the transaction state.</li>
     * </ul>
     *
     * @throws TransactionException if there is an error with the transaction
     * @throws AccountException     if there is an error with the account
     * @throws BankException        if there is an error with the bank
     */
    @Test
    public void transactionTest() throws TransactionException, AccountException, BankException {
        CentralBank cb = new ServiceCentralBank(new RewindClock(LocalDateTime.now(), new ArrayList<>()));

        List<DepositPercent> list = new ArrayList<>();
        list.add(new DepositPercent(new Percent(3), 12)); // Исправлено на создание объекта Percent

        try {
            Bank bank = cb.createBank("Sberbank", 3, list, 10,
                    200000, -1, Duration.ofDays(90));

            assertNotNull(bank);

            Clients client = cb.registerClient("Artyom", "Batmanov", "KushelevskaiaDoroga", 12345L);

            assertNotNull(client);

            BaseAccount account1 = bank.createAccount(TypeOfBankAccount.DEBIT, client, Duration.ofDays(90));

            assertNotNull(account1);

            BankTransaction transaction1 = cb.replenishAccount(bank.getId(), account1.getId(), 10000);
            assertEquals(10000, account1.getBalance());

            cb.cancelTransaction(bank.getId(), account1.getId(), transaction1.getId());
            assertEquals(State.Canceled, transaction1.getTransactionState());

            BaseAccount account2 = bank.createAccount(TypeOfBankAccount.DEBIT, client, Duration.ofDays(90));

            assertNotNull(account2);

            BankTransaction transaction2 = cb.replenishAccount(bank.getId(), account2.getId(), 500);
            assertEquals(500, account2.getBalance());

            cb.cancelTransaction(bank.getId(), account2.getId(), transaction2.getId());
            assertEquals(State.Canceled, transaction2.getTransactionState());
        } catch (BankException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
