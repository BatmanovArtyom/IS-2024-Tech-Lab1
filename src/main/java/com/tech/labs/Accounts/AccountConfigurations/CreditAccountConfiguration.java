package com.tech.labs.Accounts.AccountConfigurations;

import com.tech.labs.Exceptions.AccountException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class CreditAccountConfiguration {
    @Getter
    @Setter
    private Integer creditCommission;

    @Getter
    @Setter
    private Integer creditLimit;

    public CreditAccountConfiguration(Integer creditCommission, Integer creditLimit) throws AccountException {
        if (creditCommission < 0 || creditLimit <= 0) {
            throw new AccountException("Invalid Credit Account Configuration: commission = " + creditCommission + ", limit = " + creditLimit);
        }
        this.creditCommission = creditCommission;
        this.creditLimit = creditLimit;
    }

    public void setCreditCommission(Integer commission) throws AccountException {
        if (commission < 0) {
            throw new AccountException("CreditCommission < 0");
        }
        this.creditCommission = commission;
    }

    public void setCreditLimit(Integer limit) throws AccountException {
        if (limit < 0) {
            throw new AccountException("CreditLimit < 0");
        }
        this.creditLimit = limit;
    }
}
