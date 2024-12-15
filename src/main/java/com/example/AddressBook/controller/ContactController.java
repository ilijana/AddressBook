package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.UserEmails;
import com.example.AddressBook.repository.ContactRepository;
import io.micrometer.common.util.StringUtils;
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

  @GetMapping("/contacts/{pin}")
  public Contact getContactByPin(@PathVariable int pin) {
    return contactRepository.getContactByPin(pin);
  }

  @GetMapping("/contacts")
  public List<Contact> searchContacts(
      @RequestParam(value = "name", required = false) String nameRequest,
      @RequestParam(value = "surname", required = false) String surnameRequest,
      @RequestParam(value = "gender", required = false) String genderRequest) {

    // Use a switch expression to determine which search method to call
    switch (getNonNullParameter(nameRequest, surnameRequest, genderRequest)) {
      case "name":
        return contactRepository.searchContactsByParameter("name", nameRequest);
      case "surname":
        return contactRepository.searchContactsByParameter("surname", surnameRequest);
      case "gender":
        return contactRepository.searchContactsByParameter("gender", genderRequest);
      default:
        return contactRepository.getAllContacts();
    }
  }

  // Helper method to identify which parameter is non-null
  private String getNonNullParameter(String name, String surname, String gender) {
    if (StringUtils.isNotBlank(name)) {
      return "name";
    } else if (surname != null && !surname.isEmpty()) {
      return "surname";
    } else if (gender != null && !gender.isEmpty()) {
      return "gender";
    } else {
      return "default";
    }
  }

  // Create contact using body
  @PostMapping("/contacts/create-using-body")
  public String createContactUsingBody(@RequestBody Contact contactRequest) {
    contactRepository.createContact(
        contactRequest.getPin(),
        contactRequest.getName(),
        contactRequest.getSurname(),
        contactRequest.getGender(),
        contactRequest.getPhones(),
        contactRequest.getEmails());

    return "Contact created successfully.";
  }

  // Create contact using query parameters (URL)
  @PostMapping("/contacts/create-using-url")
  public String createContactUsingUrlParams(
      @RequestParam("pin") int pin,
      @RequestParam("name") String name,
      @RequestParam("surname") String surname,
      @RequestParam(value = "gender", required = false) String gender, // Optional gender
      @RequestParam(value = "phone", required = false) List<String> phones,
      @RequestParam(value = "email", required = false) UserEmails emails) {

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
}
