package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import com.example.AddressBook.repository.ContactRepository;
import com.example.AddressBook.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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

  private final ContactService contactService;

  public ContactController(ContactService contactService) {
    this.contactService = contactService;
  }

  @Operation(
      summary = "Find contact by PIN (Personal Identification Number).",
      description = "Retrieves a specific contact. The PIN is a required parameter.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contact found."),
        @ApiResponse(responseCode = "404", description = "No records found for the given PIN.")
      })
  @GetMapping("/contacts/findContact/{pin}")
  public ResponseEntity<Contact> getContactByPin(@PathVariable Integer pin) {
    return contactService.getContactByPin(pin);
  }

  @Operation(
      summary = "Retrieves all contacts, with the option to filter by name, surname, or gender.",
      description =
          "If request parameters are provided, contacts will be filtered according to the given values. If no parameters are provided, all contacts will be returned. Multiple filters can be applied at once.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of contacts found for the given criteria.")
      })
  @GetMapping("/contacts/findContacts")
  public ResponseEntity<List<Contact>> searchContacts(
      @RequestParam(value = "name", required = false) String nameRequest,
      @RequestParam(value = "surname", required = false) String surnameRequest,
      @RequestParam(value = "gender", required = false) Gender genderRequest) {

    return contactService.searchContacts(nameRequest, surnameRequest, genderRequest);
  }

  @Operation(
      summary = "Creates a new contact using the data provided in the JSON body.",
      description =
          "The contact details must be provided in the URL, with PIN, name, and surname being mandatory for contact creation.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contact creation was successful."),
        @ApiResponse(responseCode = "400", description = "Data integrity violation."),
        @ApiResponse(responseCode = "404", description = "Data validation failed.")
      })
  @PostMapping("/contacts/createContactFromJson")
  public ResponseEntity<String> createContactsUsingBody(
      @RequestBody List<@Valid Contact> contactsRequest) {

    return contactService.createContactsUsingBody(contactsRequest);
  }

  @Operation(
      summary = "Creates a new contact using the data provided in the URL.",
      description =
          "The contact details should be provided inside URL. PIN, name, and surname are mandatory for creating the contact.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contact creation was successful."),
        @ApiResponse(responseCode = "400", description = "Data integrity violation.")
      })
  @PostMapping("/contacts/createContact")
  public ResponseEntity<String> createContactUsingUrlParams(
      @RequestParam("pin") Integer pin,
      @RequestParam("name") String name,
      @RequestParam("surname") String surname,
      @RequestParam(value = "gender", required = false) Gender gender,
      @RequestParam(value = "phone", required = false) ContactPhones phones,
      @RequestParam(value = "email", required = false) ContactEmails emails) {

    return contactService.createContactUsingUrlParams(pin, name, surname, gender, phones, emails);
  }

  @Operation(
      summary = "Deletes contact by PIN (Personal Identification Number).",
      description = "Deletes a specific contact. The PIN is a required parameter.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Delete operation was successful."),
        @ApiResponse(responseCode = "400", description = "Data integrity violation.")
      })
  @DeleteMapping("/contacts/{pin}")
  public ResponseEntity<String> deleteContactByPin(@PathVariable Integer pin) {
    return contactService.deleteContactByPin(pin);
  }

  @Operation(
      summary =
          "Update a specific contact attribute(single or multiple) using the data provided in the JSON body.",
      description =
          "Accessing a contact using the PIN (Personal Identification Number) and updating its data only for the attributes provided in the JSON body.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Update was successful."),
        @ApiResponse(responseCode = "400", description = "Data integrity violation."),
        @ApiResponse(responseCode = "404", description = "No contact found for the given PIN.")
      })
  @PutMapping("/contacts/updateContactAttributes")
  public ResponseEntity<String> updateContact(@RequestBody Contact updateRequest) {
    return contactService.updateContact(updateRequest);
  }

  @Operation(
      summary = "Updating contact by providing attribute name and a new value for it.",
      description =
          "There is an option to provide oldValue for specific email or phone to update itd. If not provided new value will be added.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contact updated."),
        @ApiResponse(responseCode = "400", description = "Data integrity violation."),
        @ApiResponse(responseCode = "404", description = "No records for the given criteria.")
      })
  @PutMapping("/contacts/updateContactAttribute")
  public ResponseEntity<String> updateContactAttribute(
      @RequestParam("pin") Integer pin,
      @RequestParam("attribute")
          @Pattern(regexp = "name|surname|gender|emails|phones", message = "Invalid attribute type")
          String attribute,
      @RequestParam(value = "oldValue", required = false) String oldValue,
      @RequestParam("newValue") String newValue) {
    return contactService.updateContactAttribute(pin, attribute, oldValue, newValue);
  }

  @Operation(
      summary = "Delete the email associated with a specific contact.",
      description = "PIN (Personal Identification Number) and email are required parameter.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Delete operation was successful."),
        @ApiResponse(responseCode = "404", description = "Nothing found for the given criteria.")
      })
  @DeleteMapping("/contacts/deleteEmail")
  public ResponseEntity<String> deleteEmail(@RequestParam Integer pin, @RequestParam String email) {
    return contactService.deleteEmail(pin, email);
  }

  @Operation(
      summary = "Delete the phone number associated with a specific contact.",
      description = "PIN (Personal Identification Number) and phone number are required parameter.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Delete operation was successful."),
        @ApiResponse(responseCode = "404", description = "Nothing found for the given criteria.")
      })
  @DeleteMapping("/contacts/deletePhone")
  public ResponseEntity<String> deletePhone(@RequestParam Integer pin, @RequestParam String phone) {
    return contactService.deletePhone(pin, phone);
  }
}
