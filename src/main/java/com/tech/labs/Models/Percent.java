package com.tech.labs.Models;

import com.tech.labs.Exceptions.TransactionException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Percent {
    private static final double MinPercentValue = 0;
    private final double value;

    public Percent(Integer value) throws TransactionException {
        if (value < MinPercentValue)
            throw TransactionException.negativeAmount();
        this.value = value / 100;
    }
}
