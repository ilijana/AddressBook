package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.repository.ContactRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ContactController {

  private static final Logger log = LoggerFactory.getLogger(ContactController.class);

  @Autowired ContactRepository contactRepository;
  ControllerHelper controllerHelper = new ControllerHelper();

  @GetMapping("/contacts/findContact/{pin}")
  public Contact getContactByPin(@PathVariable int pin) {
    return contactRepository.getContactByPin(pin);
  }

  @GetMapping("/contacts/findContact")
  public List<Contact> searchContacts(
      @RequestParam(value = "name", required = false) String nameRequest,
      @RequestParam(value = "surname", required = false) String surnameRequest,
      @RequestParam(value = "gender", required = false) String genderRequest) {

    if (controllerHelper.checkIfRequestParamIsSet(nameRequest, surnameRequest, genderRequest)) {
      return contactRepository.searchContactsByParameter(
          nameRequest, surnameRequest, genderRequest);
    } else {
      return contactRepository.getAllContacts();
    }
  }

  @GetMapping("/contacts/findContacts")
  public List<Contact> getContactByPin() {
    return contactRepository.getAllContacts();
  }

  // Create contact using JSON body
  @PostMapping("/contacts/createContactFromJson")
  public String createContactsUsingBody(@RequestBody List<Contact> contactsRequest) {
    for (Contact contact : contactsRequest) {
      if (contact.getGender() != null && !controllerHelper.isValidGender(contact.getGender())) {
        return "Provided gender is not valid!";
      }
      contactRepository.createContact(
          contact.getPin(),
          contact.getName(),
          contact.getSurname(),
          contact.getGender(),
          contact.getPhones(),
          contact.getEmails());
    }

    return "Contacts created successfully.";
  }

  // Create contact using query parameters (URL)
  @PostMapping("/contacts/createContact")
  public String createContactUsingUrlParams(
      @RequestParam("pin") int pin,
      @RequestParam("name") String name,
      @RequestParam("surname") String surname,
      @RequestParam(value = "gender", required = false) String gender, // Optional gender
      @RequestParam(value = "phone", required = false) ContactPhones phones,
      @RequestParam(value = "email", required = false) ContactEmails emails) {

    if (gender != null && !controllerHelper.isValidGender(gender)) {
      return "Provided gender is not valid!";
    }
    // Call repository to insert the contact with phone numbers and emails
    contactRepository.createContact(pin, name, surname, gender, phones, emails);

    return "Contact created successfully.";
  }

  // Method to delete a contact by pin
  @DeleteMapping("/contacts/{pin}")
  public ResponseEntity<String> deleteContactByPin(@PathVariable int pin) {
    // Delete the contact by pin
    contactRepository.deleteContactByPin(pin);

    // Return a response indicating successful deletion
    return ResponseEntity.ok("Contact with pin " + pin + " has been deleted successfully.");
  }

  @PutMapping("/contacts/updateContact/{pin}")
  public String updateContact(@PathVariable int pin, @RequestBody Contact updateRequest) {
    if (updateRequest.getGender() != null
        && !controllerHelper.isValidGender(updateRequest.getGender())) {
      return "Provided gender is not valid!";
    }
    Contact currentContact = contactRepository.getContactByPin(pin);
    Contact updatedContact = controllerHelper.updateContact(currentContact, updateRequest);
    contactRepository.updateContactDetails(updatedContact);
    return "Contact updated successfully!";
  }

  @DeleteMapping("/contacts/deleteEmail")
  public String deleteEmail(@RequestParam int pin, @RequestParam String email) {
    contactRepository.deleteEmail(pin, email);
    return "Email deleted successfully.";
  }

  @DeleteMapping("/contacts/deletePhone")
  public String deletePhone(@RequestParam int pin, @RequestParam String phone) {
    contactRepository.deletePhone(pin, phone);
    return "Phone deleted successfully.";
  }
}
