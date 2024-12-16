package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import java.util.List;

public interface ContactRepository {

  List<Contact> getAllContacts();

  Contact getContactByPin(int pin);

  List<Contact> searchContactsByParameter(
      String nemeValue, String surnameValue, String genderValue);

  void contactCreation(
      int pin,
      String name,
      String surname,
      String gender,
      ContactPhones phones,
      ContactEmails emails);

  String deleteContactByPin(int pin);

  void updateContactDetails(Contact updatedContact);

  void deleteEmail(int pin, String email);

  void deletePhone(int pin, String phone);
}
