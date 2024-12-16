package com.example.AddressBook.model;

import lombok.Data;

@Data
public class Contact {
  private int pin;
  private String name;
  private String surname;
  private String gender;
  private ContactEmails emails;
  private ContactPhones phones;

  public Contact() {}

  @Override
  public String toString() {
    return "Contact{"
        + "pin='"
        + pin
        + '\''
        + ", name='"
        + name
        + '\''
        + ", surname='"
        + surname
        + '\''
        + ", gender='"
        + gender
        + '\''
        + ", phones='"
        + phones
        + '\''
        + ", emails='"
        + emails
        + '\''
        + '}';
  }
}
