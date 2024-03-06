package com.tech.labs.Accounts.AccountConfigurations;

import com.tech.labs.Exceptions.AccountException;
import com.tech.labs.Models.Percent;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

public class DepositPercent {
    @Getter
    private final Percent percent;

    @Getter
    private final Integer leftBorder;

    @Getter
    private final Integer rightBorder;

    /**
     * Creates a DepositPercent object with the specified parameters.
     *
     * @param percent     The percentage value for the deposit.
     * @param leftBorder  The left border of the deposit range.
     * @param rightBorder The right border of the deposit range.
     * @throws AccountException if leftBorder or rightBorder is less than 0,
     *                          or if leftBorder is greater than or equal to rightBorder.
     */
    public DepositPercent(@NonNull Percent percent, @NonNull Integer leftBorder, Integer rightBorder) throws AccountException {
        this.percent = percent;

        if (leftBorder < 0 || rightBorder < 0) {
            throw new AccountException("DepositPercent: leftBorder or rightBorder < 0");
        }

        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder.equals(Double.MAX_VALUE) ? (int) Double.MAX_VALUE : rightBorder;

        if (leftBorder.compareTo(rightBorder) >= 0) {
            throw new AccountException("DepositPercent: LeftBorder >= rightBorder");
        }
    }

    public DepositPercent(@NonNull Percent percent, Integer leftBorder) throws AccountException {
        this(percent, leftBorder, (int) Double.MAX_VALUE);
    }
}
