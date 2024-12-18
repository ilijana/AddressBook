package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;

import java.util.List;

public interface ContactRepository {

  List<Contact> getAllContacts();

  Contact getContactByPin(Integer pin);

  List<Contact> searchContactsByParameter(
      String nameValue, String surnameValue, Gender genderValue);

  void contactCreation(
      Integer pin,
      String name,
      String surname,
      Gender gender,
      ContactPhones phones,
      ContactEmails emails);

  String deleteContactByPin(Integer pin);

  void updateContactDetails(Contact updatedContact);

  void deleteEmail(Integer pin, String email);

  void deletePhone(Integer pin, String phone);

  void updateContactAttribute(Integer pin, String attribute, String newValue, String oldValue);

}
