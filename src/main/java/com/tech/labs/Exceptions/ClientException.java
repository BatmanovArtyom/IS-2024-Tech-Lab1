package com.tech.labs.Exceptions;

public class ClientException extends Exception {
    private ClientException(String message) {
        super(message);
    }

    public static ClientException invalidAddress() {
        return new ClientException("Invalid address");
    }

    public static ClientException invalidPassport(String message) {
        return new ClientException("Invalid passport\n" + message);
    }

    public static ClientException clientAlreadyExists(String id) {
        return new ClientException("Client with id: " + id + " already exists");
    }

    public static ClientException clientDoesNotExist(String id) {
        return new ClientException("Client with id: " + id + " doesn't exist");
    }
}
