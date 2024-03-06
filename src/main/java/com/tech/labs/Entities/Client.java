package com.tech.labs.Entities;

import com.tech.labs.Exceptions.ClientException;
import com.tech.labs.Interfaces.Clients;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Client implements Clients {
    private final List<String> updates = new ArrayList<>();
    private final UUID id = UUID.randomUUID();
    private final String name;
    private final String surname;
    private String address;
    private Long passportNumber;

    public Client(@NonNull String name, @NonNull String surname, String address, Long passportNumber) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.passportNumber = passportNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws ClientException {
        if (address == null || address.isEmpty()) {
            throw ClientException.invalidAddress();
        }
        this.address = address;
    }

    @Override
    public void setPassportNumber(long passport) {

    }

    public long getPassportNumber() {
        return passportNumber;
    }

    @Override
    public void setPassportNumber(Long passportNumber) throws ClientException {
        if (this.passportNumber != null) {
            throw ClientException.invalidPassport("Passport is already exist");
        }

        if (passportNumber == null || passportNumber <= 0) {
            throw ClientException.invalidPassport("Passport doesn't exist");
        }

        this.passportNumber = passportNumber;
    }

    public boolean isDubious() {
        return address == null || passportNumber == null;
    }

    public Collection<String> getUpdatesOfAccountsConfiguration() {
        return Collections.unmodifiableList(updates);
    }

    public void update(String data) {
        updates.add(data);
    }
}
