package com.tech.labs.Accounts.AccountConfigurations;

import com.tech.labs.Exceptions.TransactionException;
import com.tech.labs.Models.Percent;
import lombok.Getter;
import lombok.Setter;

public class DebitAccountConfiguration {
    @Getter
    @Setter
    private Percent debitPercent;

    public DebitAccountConfiguration(Percent debitPercent) {
        this.debitPercent = debitPercent;
    }

    public void setDebitPercent(Integer percent) throws TransactionException {
        this.debitPercent = new Percent(percent);
    }
}
