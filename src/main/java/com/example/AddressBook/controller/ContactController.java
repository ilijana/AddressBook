package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import com.example.AddressBook.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Arrays;
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

  @Operation(
      summary = "Find contact by PIN (Personal Identification Number).",
      description = "Retrieves a specific contact. The PIN is a required parameter.")
  @GetMapping("/contacts/findContact/{pin}")
  public Contact getContactByPin(@PathVariable int pin) {
    return contactRepository.getContactByPin(pin);
  }

  @Operation(
      summary = "Retrieves all contacts, with the option to filter by name, surname, or gender.",
      description =
          "If request parameters are provided, contacts will be filtered according to the given values. If no parameters are provided, all contacts will be returned. Multiple filters can be applied at once.")
  @GetMapping("/contacts/findContacts")
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

  @Operation(
      summary = "Creates a new contact using the data provided in the JSON body.",
      description =
          "The contact details must be provided in the URL, with PIN, name, and surname being mandatory for contact creation.")
  @PostMapping("/contacts/createContactFromJson")
  public String createContactsUsingBody(@RequestBody List<Contact> contactsRequest) {
    for (Contact contact : contactsRequest) {
      if (contact.getGender() != null && controllerHelper.isInvalidGender(contact.getGender())) {
        return "Provided gender is not valid! Valid genders:" + Arrays.toString(Gender.values());
      }
      contactRepository.contactCreation(
          contact.getPin(),
          contact.getName(),
          contact.getSurname(),
          contact.getGender(),
          contact.getPhones(),
          contact.getEmails());
    }

    return "Contacts created successfully.";
  }

  @Operation(
      summary = "Creates a new contact using the data provided in the URL.",
      description =
          "The contact details should be provided inside URL. PIN, name, and surname are mandatory for creating the contact.")
  @PostMapping("/contacts/createContact")
  public String createContactUsingUrlParams(
      @RequestParam("pin") int pin,
      @RequestParam("name") String name,
      @RequestParam("surname") String surname,
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "phone", required = false) ContactPhones phones,
      @RequestParam(value = "email", required = false) ContactEmails emails) {

    if (gender != null && controllerHelper.isInvalidGender(gender)) {
      return "Provided gender is not valid! Valid genders:" + Arrays.toString(Gender.values());
    }
    contactRepository.contactCreation(pin, name, surname, gender, phones, emails);

    return "Contact created successfully.";
  }

  @Operation(
      summary = "Deletes contact by PIN (Personal Identification Number).",
      description = "Deletes a specific contact. The PIN is a required parameter.")
  @DeleteMapping("/contacts/{pin}")
  public ResponseEntity<String> deleteContactByPin(@PathVariable int pin) {
    String returnMsg = contactRepository.deleteContactByPin(pin);

    return ResponseEntity.ok(returnMsg);
  }

  @Operation(
      summary = "Update a specific contact using the data provided in the JSON body.",
      description =
          "Accessing a contact using the PIN (Personal Identification Number) and updating its data, only for the attributes provided in the JSON body. If an attribute is not defined, the old value will be retained.")
  @PutMapping("/contacts/updateContact/{pin}")
  public String updateContact(@PathVariable int pin, @RequestBody Contact updateRequest) {
    if (updateRequest.getGender() != null
        && controllerHelper.isInvalidGender(updateRequest.getGender())) {
      return "Provided gender is not valid! Valid genders:" + Arrays.toString(Gender.values());
    }
    Contact currentContact = contactRepository.getContactByPin(pin);
    Contact updatedContact = controllerHelper.updateContact(currentContact, updateRequest);
    contactRepository.updateContactDetails(updatedContact);
    return "Contact updated successfully!";
  }

  @Operation(
      summary = "Delete the email associated with a specific contact",
      description = "PIN (Personal Identification Number) and email are required parameter.")
  @DeleteMapping("/contacts/deleteEmail")
  public String deleteEmail(@RequestParam int pin, @RequestParam String email) {
    contactRepository.deleteEmail(pin, email);
    return "Email deleted successfully.";
  }

  @Operation(
      summary = "Delete the phone number associated with a specific contact",
      description = "PIN (Personal Identification Number) and phone number are required parameter.")
  @DeleteMapping("/contacts/deletePhone")
  public String deletePhone(@RequestParam int pin, @RequestParam String phone) {
    contactRepository.deletePhone(pin, phone);
    return "Phone deleted successfully.";
  }
}
