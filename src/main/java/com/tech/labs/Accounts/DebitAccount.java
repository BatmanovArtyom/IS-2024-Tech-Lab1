package com.tech.labs.Accounts;

import com.tech.labs.Accounts.AccountConfigurations.BankConfiguration;
import com.tech.labs.Accounts.AccountConfigurations.DebitAccountConfiguration;
import com.tech.labs.DateTimeProvider.Clock;
import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Interfaces.Clients;
import com.tech.labs.Models.Limit;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DebitAccount extends BaseAccount {
    private final DebitAccountConfiguration configuration;
    private final Limit limitForDubiousClient;
    private final Clock clock;
    private double percentageAmount = 0;
    private int countOfDays;

    /**
     * Constructs a new Debit Account with the given clock, client, and bank configuration.
     *
     * @param clock             the clock used for date/time calculations
     * @param client            the client associated with this account
     * @param bankConfiguration the bank configuration containing account settings
     * @throws IllegalArgumentException if clock or bankConfiguration is null
     */
    public DebitAccount(Clock clock, Clients client, BankConfiguration bankConfiguration) {
        super(client, TypeOfBankAccount.DEBIT);
        if (clock == null || bankConfiguration == null) {
            throw new IllegalArgumentException("Clock and bank configuration cannot be null");
        }
        this.clock = clock;
        this.countOfDays = new GregorianCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
        this.configuration = bankConfiguration.getDebitAccountConfiguration();
        this.limitForDubiousClient = bankConfiguration.getLimitForDubiousClient();
        this.setBalance(0);
    }

    /**
     * Performs daily interest payoff for the account.
     * The account balance earns interest based on the configured debit percent.
     * The interest is calculated based on the number of days in the month.
     */
    public void accountDailyPayoff() {
        int daysInMonth = new GregorianCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
        int daysInYear = new GregorianCalendar().getActualMaximum(Calendar.DAY_OF_YEAR);
        this.percentageAmount += (getBalance() * configuration.getDebitPercent().getValue()) / daysInYear;
        this.countOfDays--;

        if (this.countOfDays != 0) {
            return;
        }

        this.setBalance((int) (getBalance() + percentageAmount));
        this.percentageAmount = 0;
        this.countOfDays = daysInMonth;
    }

    /**
     * Decreases the amount in the account by the specified sum.
     *
     * @param sum the amount to decrease
     * @throws TransactionException if the sum is negative
     * @throws AccountException     if there's not enough money or sum exceeds the limit for dubious clients
     */
    public void decreaseAmount(double sum) throws TransactionException, AccountException {
        if (sum <= 0) {
            throw new TransactionException("Negative amount");
        }

        if (getBalance() < sum) {
            throw new AccountException("Not enough money");
        }

        if (getClient().isDubious() && sum > limitForDubiousClient.getValue()) {
            throw new TransactionException("Sum exceeding the limit for dubious client");
        }

        setBalance((int) (getBalance() - sum));
    }
}
