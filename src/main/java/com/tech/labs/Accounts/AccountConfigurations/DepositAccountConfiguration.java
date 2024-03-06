package com.tech.labs.Accounts.AccountConfigurations;

import com.tech.labs.Exceptions.AccountException;
import lombok.NonNull;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class DepositAccountConfiguration {
    private List<DepositPercent> depositPercents;
    private Duration time;

    public DepositAccountConfiguration(@NonNull List<DepositPercent> depositPercents, @NonNull Duration time) throws AccountException {
        validateDepositPercents(depositPercents);
        this.depositPercents = depositPercents;
        this.time = time;
    }

    public List<DepositPercent> getDepositPercents() {
        return depositPercents;
    }

    public void setDepositPercents(List<DepositPercent> percents) throws AccountException {
        validateDepositPercents(percents);
        this.depositPercents = percents;
    }

    private void validateDepositPercents(List<DepositPercent> depositPercents) throws AccountException {
        if (Objects.requireNonNull(depositPercents).isEmpty())
            throw new AccountException("No deposit interest");
    }
}
