package com.tech.labs.Accounts.AccountConfigurations;

import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Models.Limit;
import lombok.Getter;
import lombok.Setter;


public class BankConfiguration {
    private final CreditAccountConfiguration creditAccountConfiguration;
    private final DebitAccountConfiguration debitAccountConfiguration;
    private final DepositAccountConfiguration depositAccountConfiguration;

    @Getter
    @Setter
    private Limit limitForDubiousClient;

    public BankConfiguration(CreditAccountConfiguration creditAccountConfiguration, DebitAccountConfiguration debitAccountConfiguration, DepositAccountConfiguration depositAccountConfiguration, Limit limit) {
        this.creditAccountConfiguration = creditAccountConfiguration;
        this.debitAccountConfiguration = debitAccountConfiguration;
        this.depositAccountConfiguration = depositAccountConfiguration;
    }

    public void setLimitForDubiousClient(Integer limit) throws TransactionException {
        this.limitForDubiousClient = new Limit(limit);
    }

    public CreditAccountConfiguration getCreditAccountConfiguration() {
        return creditAccountConfiguration;
    }

    public DebitAccountConfiguration getDebitAccountConfiguration() {
        return debitAccountConfiguration;
    }

    public DepositAccountConfiguration getDepositAccountConfiguration() {
        return depositAccountConfiguration;
    }
}
