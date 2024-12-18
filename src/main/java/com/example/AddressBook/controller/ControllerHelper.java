package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.Gender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerHelper {
  public Contact updateContact(Contact currentContact, Contact updateRequest) {
    if (updateRequest.getName() != null) {
      currentContact.setName(updateRequest.getName());
    }

    if (updateRequest.getSurname() != null) {
      currentContact.setSurname(updateRequest.getSurname());
    }

    if (updateRequest.getGender() != null) {
      currentContact.setGender(updateRequest.getGender());
    }

    if (updateRequest.getEmails() != null) {
      currentContact.setEmails(updateRequest.getEmails());
    }

    if (updateRequest.getPhones() != null) {
      currentContact.setPhones(updateRequest.getPhones());
    }
    return currentContact;
  }

  boolean checkIfRequestParamIsSet(String name, String surname, Gender gender) {
    return name != null
        || surname != null
        || gender != null;
  }
}
