package com.tech.labs.Service;

import com.tech.labs.Accounts.AccountConfigurations.*;
import com.tech.labs.Accounts.BaseAccount;
import com.tech.labs.Accounts.Commands.Transfer;
import com.tech.labs.Builders.ClientBuilder;
import com.tech.labs.DateTimeProvider.RewindClock;
import com.tech.labs.Entities.Bank;
import com.tech.labs.Entities.Client;
import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Exceptions.BankException;
import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Interfaces.CentralBank;
import com.tech.labs.Models.Limit;
import com.tech.labs.Models.Percent;
import com.tech.labs.Transaction.BankTransaction;
import lombok.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceCentralBank implements CentralBank {
    private final List<Bank> banks = new ArrayList<>();
    private final List<Client> clients = new ArrayList<>();
    private final RewindClock rewindClock;

    public ServiceCentralBank(@NonNull RewindClock rewindClock) {
        this.rewindClock = rewindClock;
    }

    @Override
    public RewindClock getRewindClock() {
        return rewindClock;
    }

    @Override
    public Client registerClient(String name, String surname, String address, Long passport) {
        Client client = new ClientBuilder()
                .addName(name)
                .addSurname(surname)
                .addAddress(address)
                .addPassportNumber(passport)
                .build();
        clients.add(client);
        return client;
    }

    @Override
    public Bank findBankByName(String name) throws BankException {
        return banks.stream()
                .filter(bank -> bank.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new BankException("Bank with name: " + name + " doesn't exist"));
    }

    /**
     * Creates a new Bank entity with the specified parameters.
     *
     * @param name                  the name of the new bank
     * @param debitPercent          the debit percent for debit accounts
     * @param depositPercents       the list of deposit percents
     * @param creditCommission      the credit commission for credit accounts
     * @param creditLimit           the credit limit for credit accounts
     * @param limitForDubiousClient the limit for dubious clients
     * @param endOfPeriod           the end of the deposit account's period
     * @return the created Bank instance
     * @throws BankException        if a bank with the same name already exists
     * @throws AccountException     if there is an error with the account
     * @throws TransactionException if there is an error with the transaction
     */
    @Override
    public Bank createBank(String name, double debitPercent, List<DepositPercent> depositPercents,
                           double creditCommission, double creditLimit, double limitForDubiousClient,
                           Duration endOfPeriod) throws BankException, AccountException, TransactionException {
        if (banks.stream().anyMatch(bank -> bank.getName().equals(name))) {
            throw new BankException("Bank with name: " + name + " already exists");
        }

        CreditAccountConfiguration credit = new CreditAccountConfiguration((int) creditCommission, (int) creditLimit);
        DebitAccountConfiguration debit = new DebitAccountConfiguration(new Percent((int) debitPercent));
        DepositAccountConfiguration deposit = new DepositAccountConfiguration(depositPercents, endOfPeriod);
        BankConfiguration bankConfiguration = new BankConfiguration(credit, debit, deposit,
                new Limit((long) limitForDubiousClient));

        Bank bank = new Bank(name, rewindClock, bankConfiguration);
        banks.add(bank);
        return bank;
    }

    /**
     * Initiates an income transaction to replenish the specified account.
     *
     * @param bankId   the ID of the bank where the account is located
     * @param accountId the ID of the account to replenish
     * @param amount   the amount of money to add to the account
     * @return the transaction details
     * @throws TransactionException if there is an error with the transaction
     * @throws AccountException     if there is an error with the account
     * @throws BankException        if there is an error with the bank
     */
    @Override
    public BankTransaction replenishAccount(UUID bankId, UUID accountId, double amount) throws TransactionException, AccountException, BankException {
        Bank bank = findBankById(bankId);
        return bank.income(accountId, (int) amount);
    }


    /**
     * Initiates a withdrawal transaction from the specified account.
     *
     * @param bankId   the ID of the bank where the account is located
     * @param accountId the ID of the account to withdraw from
     * @param amount   the amount of money to withdraw
     * @return the transaction details
     * @throws TransactionException if there is an error with the transaction
     * @throws AccountException     if there is an error with the account
     * @throws BankException        if there is an error with the bank
     */
    @Override
    public BankTransaction withdrawMoney(UUID bankId, UUID accountId, double amount) throws TransactionException, AccountException, BankException {
        Bank bank = findBankById(bankId);
        return bank.withdraw(accountId, (int) amount);
    }


    /**
     * Initiates a money transfer transaction between two accounts in different banks.
     *
     * @param bankId1   the ID of the bank where the first account is located
     * @param accountId1 the ID of the first account to transfer money from
     * @param bankId2   the ID of the bank where the second account is located
     * @param accountId2 the ID of the second account to transfer money to
     * @param amount    the amount of money to transfer
     * @return the transaction details
     * @throws AccountException     if there is an error with the accounts
     * @throws TransactionException if there is an error with the transaction
     * @throws BankException        if there is an error with the banks
     */
    @Override
    public BankTransaction transferMoney(UUID bankId1, UUID accountId1, UUID bankId2, UUID accountId2, double amount) throws AccountException, TransactionException, BankException {
        Bank bank1 = findBankById(bankId1);
        Bank bank2 = findBankById(bankId2);
        BaseAccount fromAccount = bank1.getAccount(accountId1);
        BaseAccount toAccount = bank2.getAccount(accountId2);

        BankTransaction transaction = new BankTransaction(new Transfer(toAccount, fromAccount, (int) amount));
        transaction.doTransaction();
        toAccount.saveChanges(transaction);
        fromAccount.saveChanges(transaction);

        return transaction;
    }

    @Override
    public void cancelTransaction(UUID bankId, UUID accountId, UUID transactionId) throws AccountException, TransactionException, BankException {
        Bank bank = findBankById(bankId);
        bank.getAccount(accountId).getTransaction(transactionId).undo();
    }

    private Bank findBankById(UUID bankId) throws BankException {
        return banks.stream()
                .filter(bank -> bank.getId().equals(bankId))
                .findFirst()
                .orElseThrow(() -> new BankException("Bank with ID: " + bankId.toString() + " doesn't exist"));
    }
}
