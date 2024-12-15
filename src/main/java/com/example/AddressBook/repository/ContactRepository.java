package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.UserEmails;
import java.util.List;

public interface ContactRepository {

  List<Contact> getAllContacts();

  Contact getContactByPin(int pin);

  List<Contact> searchContactsByParameter(String parameter, String value);

  void createContact(
      int pin,
      String name,
      String surname,
      String gender,
      List<String> phoneNumbers,
      UserEmails emails);

  void deleteContactByPin(int pin);
}
