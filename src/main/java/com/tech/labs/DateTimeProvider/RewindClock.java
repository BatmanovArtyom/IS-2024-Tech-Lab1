package com.tech.labs.DateTimeProvider;

import com.tech.labs.Exceptions.AccountException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public class RewindClock implements Clock {
    @Getter
    @Setter
    private LocalDateTime currentTime;

    private List<Runnable> actions = new ArrayList<>();

    /**
     * Rewinds the time of the account by the specified amount and unit.
     * This method simulates going back in time for the account.
     *
     * @param unit   the unit of time to rewind (e.g., days, months)
     * @param amount the amount of time to rewind
     * @throws AccountException if the amount is negative or the new time is before the current time
     */
    public void rewindTime(ChronoUnit unit, long amount) throws AccountException {
        if (amount < 0) {
            throw new AccountException("Invalid period of account");
        }

        LocalDateTime newTime = this.currentTime.minus(amount, unit);
        if (newTime.isBefore(LocalDateTime.now())) {
            throw new AccountException("Cannot rewind time to the past");
        }

        this.currentTime = newTime;
        actions.forEach(Runnable::run);
    }

    @Override
    public LocalDateTime currentTime() {
        return null;
    }

    @Override
    public void addAction(Consumer<LocalDateTime> action) {

    }
}
