package com.example.AddressBook.service;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import com.example.AddressBook.repository.ContactRepository;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactService {

  private final ContactRepository contactRepository;

  public ContactService(ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  public Contact updateContactWithNewValues(Contact currentContact, Contact updateRequest) {
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

  public boolean checkIfRequestParamIsSet(String name, String surname, Gender gender) {
    return name != null || surname != null || gender != null;
  }

  public ResponseEntity<String> updateContactAttribute(
      Integer pin, String attribute, String oldValue, String newValue) {
    Contact existingContact = contactRepository.getContactByPin(pin);
    if (existingContact == null) {
      return ResponseEntity.status(404).body("Contact with pin " + pin + " not found.");
    }
    if (attribute.equals("phones") && !isValidPhoneNumber(newValue)) {
      return ResponseEntity.status(400).body("Provided phone number is not valid");
    }
    if (attribute.equals("emails") && !isValidEmail(newValue)) {
      return ResponseEntity.status(400).body("Provided email number is not valid");
    }
    contactRepository.updateContactAttribute(pin, attribute, newValue, oldValue);
    return ResponseEntity.ok("Contact updated.");
  }

  public static boolean isValidPhoneNumber(String phoneNumber) {
    String PHONE_REGEX =
        "^\\+?[0-9]{1,4}?[-. \\(\\)]?(\\d{1,3})?[-. \\(\\)]?\\d{1,3}[-. \\(\\)]?\\d{4}$";
    if (phoneNumber == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(PHONE_REGEX);
    return pattern.matcher(phoneNumber).matches();
  }

  public static boolean isValidEmail(String email) {
    String PHONE_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    if (email == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(PHONE_REGEX);
    return pattern.matcher(email).matches();
  }

  public ResponseEntity<List<Contact>> searchContacts(
      String nameRequest, String surnameRequest, Gender genderRequest) {
    if (checkIfRequestParamIsSet(nameRequest, surnameRequest, genderRequest)) {
      return ResponseEntity.ok(
          contactRepository.searchContactsByParameter(nameRequest, surnameRequest, genderRequest));
    } else {
      return ResponseEntity.ok(contactRepository.getAllContacts());
    }
  }

  public ResponseEntity<Contact> getContactByPin(Integer pin) {
    return ResponseEntity.ok(contactRepository.getContactByPin(pin));
  }

  public ResponseEntity<String> createContactsUsingBody(List<Contact> contactsRequest) {
    for (Contact contact : contactsRequest) {
      contactRepository.contactCreation(
          contact.getPin(),
          contact.getName(),
          contact.getSurname(),
          contact.getGender(),
          contact.getPhones(),
          contact.getEmails());
    }
    return ResponseEntity.ok("Contacts created successfully.");
  }

  public ResponseEntity<String> createContactUsingUrlParams(
      Integer pin,
      String name,
      String surname,
      Gender gender,
      ContactPhones phones,
      ContactEmails emails) {
    contactRepository.contactCreation(pin, name, surname, gender, phones, emails);
    return ResponseEntity.ok("Contact created successfully.");
  }

  public ResponseEntity<String> deleteContactByPin(Integer pin) {
    String returnMsg = contactRepository.deleteContactByPin(pin);
    return ResponseEntity.ok(returnMsg);
  }

  public ResponseEntity<String> updateContact(Contact updateRequest) {
    if (updateRequest.getPin() == null) {
      return ResponseEntity.ofNullable("PIN is mandatory.");
    }
    Contact currentContact = contactRepository.getContactByPin(updateRequest.getPin());
    Contact updatedContact = updateContactWithNewValues(currentContact, updateRequest);
    contactRepository.updateContactDetails(updatedContact, updateRequest);
    return ResponseEntity.ok("Contact updated successfully!");
  }

  public ResponseEntity<String> deleteEmail(Integer pin, String email) {
    contactRepository.deleteEmail(pin, email);
    return ResponseEntity.ok("Email deleted successfully.");
  }

  public ResponseEntity<String> deletePhone(Integer pin, String phone) {
    contactRepository.deletePhone(pin, phone);
    return ResponseEntity.ok("Phone deleted successfully.");
  }
}
