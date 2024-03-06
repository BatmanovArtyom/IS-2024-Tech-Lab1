package com.tech.labs.Entities;

import com.tech.labs.Accounts.AccountConfigurations.BankConfiguration;
import com.tech.labs.Accounts.AccountConfigurations.DepositPercent;
import com.tech.labs.Accounts.*;
import com.tech.labs.Accounts.Commands.BalanceOperationCommand;
import com.tech.labs.Accounts.Commands.Income;
import com.tech.labs.Accounts.Commands.Withdraw;
import com.tech.labs.DateTimeProvider.Clock;
import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Exceptions.BankException;
import com.tech.labs.Exceptions.ObserverException;
import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Interfaces.Clients;
import com.tech.labs.Observer.Observer;
import com.tech.labs.Transaction.BankTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class Bank {
    @Getter
    private final UUID id;
    @Getter
    private final String name;
    private final List<BaseAccount> bankAccounts = new ArrayList<>();
    private final List<Observer<String>> subscribers = new ArrayList<Observer<String>>();
    private final BankConfiguration bankConfiguration;
    private final Clock clock;


    /**
     * Constructs a new Bank with the given name, clock, and bank configuration.
     *
     * @param name              the name of the bank
     * @param clock             the clock for managing time-related operations
     * @param bankConfiguration the configuration settings for the bank
     * @throws IllegalArgumentException if the name is null or empty
     */
    public Bank(String name,
                Clock clock, BankConfiguration bankConfiguration) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        this.id = UUID.randomUUID();
        this.clock = clock;
        this.bankConfiguration = bankConfiguration;
    }

    /**
     * Finds an account in the bank by its ID.
     *
     * @param accountId the ID of the account to find
     * @return an Optional containing the found account, or empty if not found
     */
    public Optional<BaseAccount> findAccount(UUID accountId) {
        return bankAccounts.stream()
                .filter(acc -> acc.getId().equals(accountId))
                .findFirst();
    }


    /**
     * Gets an account from the bank by its ID.
     *
     * @param accountId the ID of the account to get
     * @return the account with the specified ID
     * @throws AccountException if the account does not exist
     */
    public BaseAccount getAccount(UUID accountId) throws AccountException {
        return findAccount(accountId)
                .orElseThrow(() -> new AccountException("Account does not exist with ID: " + accountId));
    }

    public void subscribe(Observer<String> observer) throws ObserverException {
        if (observer == null) {
            throw new NullPointerException("Observer cannot be null");
        }

        if (subscribers.contains(observer)) {
            throw ObserverException.subscribeAlreadyExists();
        }

        subscribers.add(observer);
    }

    public void unsubscribe(Observer<String> observer) throws ObserverException {
        if (observer == null) {
            throw new NullPointerException("Observer cannot be null");
        }
        if (!subscribers.remove(observer)) {
            throw ObserverException.subscribeDoesNotExist();
        }
    }


    /**
     * Creates a new bank account of the specified type for the given client.
     *
     * @param typeOfBankAccount the type of bank account to create
     * @param client            the client for whom the account is created
     * @param endOfPeriod       the end of the deposit account's period
     * @return the created bank account
     * @throws BankException if there is an error creating the account
     */
    public BaseAccount createAccount(TypeOfBankAccount typeOfBankAccount, Clients client, Duration endOfPeriod) throws BankException {
        BaseAccount account;
        switch (typeOfBankAccount) {
            case CREDIT:
                account = new CreditAccount(client, bankConfiguration);
                break;
            case DEBIT:
                account = new DebitAccount(clock, client, bankConfiguration);
                break;
            case DEPOSIT:
                account = new DepositAccount(clock, client, bankConfiguration, endOfPeriod);
                break;
            default:
                throw new IllegalArgumentException("Invalid type of bank account");
        }
        bankAccounts.add(account);
        return account;
    }


    /**
     * Changes the debit percent for the bank's debit accounts.
     *
     * @param percent the new debit percent
     * @throws TransactionException if there is an error changing the percent
     */
    public void changeDebitPercent(Integer percent) throws TransactionException {
        bankConfiguration.getDebitAccountConfiguration().setDebitPercent(percent);
        notifyClients(TypeOfBankAccount.DEBIT, "New debit percent: " + percent + "%");
    }


    /**
     * Changes the deposit percents for the bank's deposit accounts.
     *
     * @param depositPercents the new deposit percents
     * @throws AccountException if there is an error changing the percents
     */
    public void changeDepositPercents(List<DepositPercent> depositPercents) throws AccountException {
        bankConfiguration.getDepositAccountConfiguration().setDepositPercents(depositPercents);
        StringBuilder percents = new StringBuilder();
        depositPercents.forEach(dp -> percents.append(dp.getLeftBorder())
                .append(" - ")
                .append(dp.getRightBorder())
                .append(": ")
                .append(dp.getPercent().getValue())
                .append("\n"));
        notifyClients(TypeOfBankAccount.DEPOSIT, "New deposit percents:\n" + percents.toString());
    }

    public void changeCreditCommission(Integer commission) throws AccountException {
        bankConfiguration.getCreditAccountConfiguration().setCreditCommission(commission);
        notifyClients(TypeOfBankAccount.CREDIT, "New credit commission: " + commission);
    }

    public void changeCreditLimit(Integer creditLimit) throws AccountException {
        bankConfiguration.getCreditAccountConfiguration().setCreditLimit(creditLimit);
        notifyClients(TypeOfBankAccount.CREDIT, "New credit limit: " + creditLimit);
    }

    public void changeLimitForDubiousClient(Integer limitForDubiousClient) throws TransactionException {
        bankConfiguration.setLimitForDubiousClient(limitForDubiousClient);
        notifyClients(TypeOfBankAccount.CREDIT, "New limit for dubious client: " + limitForDubiousClient);
        notifyClients(TypeOfBankAccount.DEBIT, "New limit for dubious client: " + limitForDubiousClient);
        notifyClients(TypeOfBankAccount.DEPOSIT, "New limit for dubious client: " + limitForDubiousClient);
    }


    /**
     * Initiates an income transaction for the specified account.
     *
     * @param accountId the ID of the account to receive the income
     * @param sum       the amount of money to add to the account
     * @return the transaction details
     * @throws TransactionException if there is an error with the transaction
     * @throws AccountException     if there is an error with the account
     */
    public BankTransaction income(UUID accountId, Integer sum) throws TransactionException, AccountException {
        BaseAccount account = getAccount(accountId);
        BalanceOperationCommand incomeCommand = new Income(account, sum);
        BankTransaction transaction = new BankTransaction(incomeCommand);
        transaction.doTransaction();
        account.saveChanges(transaction);
        return transaction;
    }


    /**
     * Initiates a withdrawal transaction for the specified account.
     *
     * @param accountId the ID of the account to withdraw from
     * @param sum       the amount of money to withdraw
     * @return the transaction details
     * @throws AccountException     if there is an error with the account
     * @throws TransactionException if there is an error with the transaction
     */
    public BankTransaction withdraw(UUID accountId, Integer sum) throws AccountException, TransactionException {
        BaseAccount account = getAccount(accountId);
        BalanceOperationCommand withdrawCommand = new Withdraw(account, sum);
        BankTransaction transaction = new BankTransaction(withdrawCommand);
        transaction.doTransaction();
        account.saveChanges(transaction);
        return transaction;
    }

    /**
     * Notifies subscribed clients about changes in the bank.
     *
     * @param selectType the type of bank account to notify clients about
     * @param data       the data to send to clients
     */
    private void notifyClients(TypeOfBankAccount selectType, String data) {
        bankAccounts.stream()
                .filter(acc -> acc.getType().equals(selectType))
                .filter(acc -> subscribers.contains(acc.getClient()))
                .distinct()
                .map(BaseAccount::getClient)
                .forEach(client -> client.update(data));
    }
}