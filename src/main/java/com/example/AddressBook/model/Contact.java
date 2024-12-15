package com.example.AddressBook.model;


import lombok.Data;

import java.util.List;

@Data
public class Contact {
    private int pin;
    private String name;
    private String surname;
    private String gender;
    private UserEmails emails;  // List of emails as a single string
    private List<String> phones;  // List of phone numbers as a single string

    public Contact() {
    }

    @Override
    public String toString() {
        return "Contact{" +
                "pin='" + pin + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", gender='" + gender + '\'' +
                ", phones='" + phones + '\'' +
                ", emails='" + emails + '\'' +
                '}';
    }
}