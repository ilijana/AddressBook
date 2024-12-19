package com.example.AddressBook.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Contact {
  @NotNull(message = "Must be provided as an argument!")
  private Integer pin;

  @NotNull(message = "name is mandatory")
  private String name;

  @NotNull(message = "surname is mandatory")
  private String surname;

  private Gender gender;
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
