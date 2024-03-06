package com.tech.labs.Builders;

import com.tech.labs.Entities.Client;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientBuilder {
    private String name;
    private String surname;
    private String address;
    private Long passportNumber;

    public ClientBuilder addName(String name) {
        this.name = name;
        return this;
    }

    public ClientBuilder addSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public ClientBuilder addAddress(String address) {
        this.address = address;
        return this;
    }

    public ClientBuilder addPassportNumber(Long passportNumber) {
        this.passportNumber = passportNumber;
        return this;
    }

    public Client build() {
        return new Client(
                name,
                surname,
                address,
                passportNumber
        );
    }
}
