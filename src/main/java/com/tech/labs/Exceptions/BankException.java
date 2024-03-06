package com.tech.labs.Exceptions;

public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }

    public static BankException bankAlreadyExists(String name) {
        return new BankException("Bank with name: " + name + " already exists");
    }

    public static BankException bankDoesNotExist(String name) {
        return new BankException("Bank with name: " + name + " doesn't exist");
    }
}
