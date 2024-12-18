package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import com.example.AddressBook.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Contact found."),
          @ApiResponse(responseCode = "404", description = "No records found for the given PIN.")
  })
  public ResponseEntity<Contact> getContactByPin(@PathVariable Integer pin) {
    return ResponseEntity.ok(contactRepository.getContactByPin(pin));
  }

  @Operation(
      summary = "Retrieves all contacts, with the option to filter by name, surname, or gender.",
      description =
          "If request parameters are provided, contacts will be filtered according to the given values. If no parameters are provided, all contacts will be returned. Multiple filters can be applied at once.")
  @GetMapping("/contacts/findContacts")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of contacts found for the given criteria.")
  })
  public ResponseEntity<List<Contact>> searchContacts(
      @RequestParam(value = "name", required = false) String nameRequest,
      @RequestParam(value = "surname", required = false) String surnameRequest,
      @RequestParam(value = "gender", required = false) Gender genderRequest) {

    if (controllerHelper.checkIfRequestParamIsSet(nameRequest, surnameRequest, genderRequest)) {
      return ResponseEntity.ok(contactRepository.searchContactsByParameter(
          nameRequest, surnameRequest, genderRequest));
    } else {
      return ResponseEntity.ok(contactRepository.getAllContacts());
    }
  }

  @Operation(
      summary = "Creates a new contact using the data provided in the JSON body.",
      description =
          "The contact details must be provided in the URL, with PIN, name, and surname being mandatory for contact creation.")
  @PostMapping("/contacts/createContactFromJson")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Contact creation was successful."),
          @ApiResponse(responseCode = "400", description = "Data integrity violation."),
          @ApiResponse(responseCode = "404", description = "Data validation failed.")
  })
  public ResponseEntity<String> createContactsUsingBody(@RequestBody List<@Valid Contact> contactsRequest) {
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

  @Operation(
      summary = "Creates a new contact using the data provided in the URL.",
      description =
          "The contact details should be provided inside URL. PIN, name, and surname are mandatory for creating the contact.")
  @PostMapping("/contacts/createContact")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Contact creation was successful."),
          @ApiResponse(responseCode = "400", description = "Data integrity violation.")
  })
  public ResponseEntity<String> createContactUsingUrlParams(
      @RequestParam("pin") Integer pin,
      @RequestParam("name") String name,
      @RequestParam("surname") String surname,
      @RequestParam(value = "gender", required = false) Gender gender,
      @RequestParam(value = "phone", required = false) ContactPhones phones,
      @RequestParam(value = "email", required = false) ContactEmails emails) {

    contactRepository.contactCreation(pin, name, surname, gender, phones, emails);

    return ResponseEntity.ok("Contact created successfully.");
  }

  @Operation(
      summary = "Deletes contact by PIN (Personal Identification Number).",
      description = "Deletes a specific contact. The PIN is a required parameter.")
  @DeleteMapping("/contacts/{pin}")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Delete operation was successful."),
          @ApiResponse(responseCode = "400", description = "Data integrity violation.")
  })
  public ResponseEntity<String> deleteContactByPin(@PathVariable Integer pin) {
    String returnMsg = contactRepository.deleteContactByPin(pin);

    return ResponseEntity.ok(returnMsg);
  }

  @Operation(
      summary = "Update a specific contact using the data provided in the JSON body.",
      description =
          "Accessing a contact using the PIN (Personal Identification Number) and updating its data, only for the attributes provided in the JSON body. If an attribute is not defined, the old value will be retained.")
  @PutMapping("/contacts/updateContact/{pin}")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Update was successful."),
          @ApiResponse(responseCode = "400", description = "Data integrity violation.")
  })
  public ResponseEntity<String> updateContact(@PathVariable Integer pin, @RequestBody Contact updateRequest) {
    Contact currentContact = contactRepository.getContactByPin(pin);
    Contact updatedContact = controllerHelper.updateContact(currentContact, updateRequest);
    contactRepository.updateContactDetails(updatedContact);
    return ResponseEntity.ok("Contact updated successfully!");
  }

  @Operation(
      summary = "Delete the email associated with a specific contact",
      description = "PIN (Personal Identification Number) and email are required parameter.")
  @DeleteMapping("/contacts/deleteEmail")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Delete operation was successful."),
          @ApiResponse(responseCode = "404", description = "Nothing found for the given criteria.")
  })
  public ResponseEntity<String> deleteEmail(@RequestParam Integer pin, @RequestParam String email) {
    contactRepository.deleteEmail(pin, email);
    return ResponseEntity.ok("Email deleted successfully.");
  }

  @Operation(
      summary = "Delete the phone number associated with a specific contact",
      description = "PIN (Personal Identification Number) and phone number are required parameter.")
  @DeleteMapping("/contacts/deletePhone")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Delete operation was successful."),
          @ApiResponse(responseCode = "404", description = "Nothing found for the given criteria.")
  })
  public ResponseEntity<String> deletePhone(@RequestParam Integer pin, @RequestParam String phone) {
    contactRepository.deletePhone(pin, phone);
    return ResponseEntity.ok("Phone deleted successfully.");
  }
}
