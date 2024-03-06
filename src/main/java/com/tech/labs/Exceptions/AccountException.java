package com.tech.labs.Exceptions;

import java.util.UUID;

public class AccountException extends Exception {
    public AccountException(String message) {
        super(message);
    }

    public static AccountException invalidConfiguration(String message) {
        return new AccountException(message);
    }

    public static AccountException notEnoughMoney() {
        return new AccountException("Don't have enough money on account");
    }

    public static AccountException invalidPeriodOfAccount() {
        return new AccountException("Period of the account is invalid");
    }

    public static AccountException expiration(UUID id) {
        return new AccountException("Account with id: " + id.toString() + " expired");
    }

    public static AccountException accountAlreadyExists(UUID id) {
        return new AccountException("Account with id: " + id.toString() + " already exists");
    }

    public static AccountException accountDoesNotExist(UUID id) {
        return new AccountException("Account with id: " + id.toString() + " doesn't exist");
    }
}
