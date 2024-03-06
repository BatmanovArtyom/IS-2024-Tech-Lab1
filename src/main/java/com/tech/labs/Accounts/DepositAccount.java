package com.tech.labs.Accounts;

import com.tech.labs.Accounts.AccountConfigurations.BankConfiguration;
import com.tech.labs.Accounts.AccountConfigurations.DepositAccountConfiguration;
import com.tech.labs.Accounts.AccountConfigurations.DepositPercent;
import com.tech.labs.DateTimeProvider.Clock;
import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Interfaces.Clients;
import com.tech.labs.Models.Limit;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

public class DepositAccount extends BaseAccount {
    private final DepositAccountConfiguration configuration;
    private final Limit limitForDubiousClient;
    private final LocalDateTime endOfPeriod;
    private final Clock clock;
    private double percentageAmount = 0;

    /**
     * Constructs a new Deposit Account with the given clock, client, bank configuration, and end of period.
     *
     * @param clock             the clock used for date/time calculations
     * @param client            the client associated with this account
     * @param bankConfiguration the bank configuration containing account settings
     * @param endOfPeriod       the end of the deposit period
     * @throws IllegalArgumentException if clock or bankConfiguration is null
     */
    public DepositAccount(Clock clock, Clients client, BankConfiguration bankConfiguration, Duration endOfPeriod) {
        super(client, TypeOfBankAccount.DEPOSIT);
        if (clock == null || bankConfiguration == null) {
            throw new IllegalArgumentException("Clock and bank configuration cannot be null");
        }
        this.clock = clock;
        this.configuration = bankConfiguration.getDepositAccountConfiguration();
        this.limitForDubiousClient = bankConfiguration.getLimitForDubiousClient();
        this.endOfPeriod = LocalDateTime.now().plus(endOfPeriod);
        this.setBalance(0);
    }

    /**
     * Performs daily interest payoff for the deposit account.
     * The account balance earns interest based on the configured deposit percent.
     * The interest is calculated based on the balance and the suitable deposit percent for the balance.
     *
     * @throws AccountException if the current time is after the end of the deposit period or no suitable deposit percent is found
     */
    public void accountDailyPayoff() throws AccountException {
        LocalDateTime currentTime = clock.currentTime();
        if (currentTime.isAfter(endOfPeriod)) {
            throw AccountException.invalidPeriodOfAccount();
        }

        if (currentTime.equals(endOfPeriod)) {
            this.setBalance((int) (this.getBalance() + percentageAmount));
            this.percentageAmount = 0;
            return;
        }

        DepositPercent depositPercent = this.configuration.getDepositPercents()
                .stream()
                .filter(dp -> dp.getLeftBorder() <= this.getBalance() && dp.getRightBorder() > this.getBalance())
                .findFirst()
                .orElseThrow(() -> new AccountException("No suitable deposit percent found"));

        int daysInYear = currentTime.toLocalDate().lengthOfYear();
        this.percentageAmount += (this.getBalance() * depositPercent.getPercent().getValue() * 100) / daysInYear;
    }

    /**
     * Decreases the amount in the account by the specified sum.
     *
     * @param sum the amount to decrease
     * @throws TransactionException if the sum is negative or the account has not yet expired
     * @throws AccountException     if there's not enough money or sum exceeds the limit for dubious clients
     */
    public void decreaseAmount(double sum) throws TransactionException, AccountException {
        if (sum <= 0) {
            throw new TransactionException("Negative amount");
        }

        if (clock.currentTime().isBefore(endOfPeriod)) {
            throw new AccountException("Account has not yet expired");
        }

        if (getClient().isDubious() && sum > limitForDubiousClient.getValue()) {
            throw new TransactionException("Sum exceeding the limit for dubious client");
        }

        if (this.getBalance() < sum) {
            throw new AccountException("Not enough money");
        }

        this.setBalance((int) (this.getBalance() - sum));
    }
}
