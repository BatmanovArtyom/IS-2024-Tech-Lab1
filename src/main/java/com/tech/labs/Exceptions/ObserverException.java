package com.tech.labs.Exceptions;

public class ObserverException extends Exception {
    private ObserverException(String message) {
        super(message);
    }

    public static ObserverException subscribeAlreadyExists() {
        return new ObserverException("Subscribe already exists");
    }

    public static ObserverException subscribeDoesNotExist() {
        return new ObserverException("Subscribe doesn't exist");
    }
}
