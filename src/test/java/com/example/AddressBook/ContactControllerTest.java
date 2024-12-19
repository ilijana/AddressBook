package com.example.AddressBook;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import com.example.AddressBook.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {

  private final String URL = "http://localhost:8080/api";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ContactService contactService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCreateContactsUsingBodySuccess() throws Exception {
    Contact testContact = new Contact();
    testContact.setPin(1234);
    testContact.setName("Test");
    testContact.setSurname("Test");
    testContact.setGender(Gender.FEMALE);

    List<Contact> contactsRequest = List.of(testContact);

    String jsonRequest = objectMapper.writeValueAsString(contactsRequest);

    when(contactService.createContactsUsingBody(contactsRequest))
        .thenReturn(ResponseEntity.ok("Contacts created successfully."));

    mockMvc
        .perform(
            post(URL + "/contacts/createContactFromJson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(content().string("Contacts created successfully."));

    verify(contactService, times(1)).createContactsUsingBody(contactsRequest);
  }

  @Test
  public void testCreateContactsUsingBodyFail() throws Exception {
    Contact testContact = new Contact();
    testContact.setPin(1234);
    testContact.setName("Test");
    testContact.setGender(Gender.FEMALE);

    List<Contact> contactsRequest = List.of(testContact);

    String jsonRequest = objectMapper.writeValueAsString(contactsRequest);

    when(contactService.createContactsUsingBody(contactsRequest))
        .thenReturn(
            new ResponseEntity<>(
                "Validation failed! surname: surname is mandatory", HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            post(URL + "/contacts/createContactFromJson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Validation failed! surname: surname is mandatory"));

    verify(contactService, times(0)).createContactsUsingBody(contactsRequest);
  }

  @Test
  public void testCreateContactUsingUrlParamsSuccess() throws Exception {
    Integer pin = 1234;
    String name = "test";
    String surname = "test";
    Gender gender = Gender.FEMALE;
    ContactPhones phone = new ContactPhones();
    phone.add("12345-67890");

    when(contactService.createContactUsingUrlParams(pin, name, surname, gender, phone, null))
        .thenReturn(ResponseEntity.ok("Contact created successfully."));

    mockMvc
        .perform(
            post(URL + "/contacts/createContact")
                .param("pin", String.valueOf(pin))
                .param("name", name)
                .param("surname", surname)
                .param("gender", gender.name())
                .param("phone", "12345-67890")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Contact created successfully."));

    verify(contactService, times(1))
        .createContactUsingUrlParams(pin, name, surname, gender, phone, null);
  }

  @Test
  public void testCreateContactUsingUrlParamsNoPin() throws Exception {
    String name = "test";
    String surname = "test";
    Gender gender = Gender.FEMALE;
    ContactPhones phone = new ContactPhones();
    phone.add("12345-67890");

    mockMvc
        .perform(
            post(URL + "/contacts/createContact")
                .param("name", name)
                .param("surname", surname)
                .param("gender", gender.name())
                .param("phone", "12345-67890")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(
            content()
                .string(
                    containsString(
                        "Required request parameter 'pin' for method parameter type Integer is not present")));
  }

  @Test
  public void testGetContactByPinSuccess() throws Exception {
    Integer pin = 123456789;

    Contact mockContact = new Contact();
    mockContact.setPin(pin);
    mockContact.setName("test");
    mockContact.setSurname("test");
    mockContact.setGender(Gender.FEMALE);

    when(contactService.getContactByPin(pin)).thenReturn(ResponseEntity.ok(mockContact));

    mockMvc
        .perform(get(URL + "/contacts/findContact/{pin}", pin))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pin").value(pin))
        .andExpect(jsonPath("$.name").value("test"))
        .andExpect(jsonPath("$.surname").value("test"))
        .andExpect(jsonPath("$.gender").value("FEMALE"));

    verify(contactService, times(1)).getContactByPin(pin);
  }

  @Test
  public void testGetContactByPinNotFound() throws Exception {
    Integer pin = 123456;

    when(contactService.getContactByPin(pin))
        .thenThrow(
            new EmptyResultDataAccessException("No records found for the given criteria.", 1));

    mockMvc
        .perform(get(URL + "/contacts/findContact/{pin}", pin))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No records found for the given criteria."));

    verify(contactService, times(1)).getContactByPin(pin);
  }

  @Test
  public void testUpdateContactSuccess() throws Exception {
    Contact updateRequest = new Contact();
    updateRequest.setPin(1234);
    updateRequest.setName("UpdatedName");
    updateRequest.setSurname("UpdatedSurname");
    updateRequest.setGender(Gender.MALE);

    when(contactService.updateContact(updateRequest))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Contact updated successfully"));

    String jsonRequest = objectMapper.writeValueAsString(updateRequest);

    mockMvc
        .perform(
            put(URL + "/contacts/updateContactAttributes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(content().string("Contact updated successfully"));
    verify(contactService, times(1)).updateContact(updateRequest);
  }

  @Test
  public void testUpdateContactFail() throws Exception {
    Contact updateRequest = new Contact();
    updateRequest.setPin(1234);
    updateRequest.setName("UpdatedName");
    updateRequest.setSurname("UpdatedSurname");
    updateRequest.setGender(Gender.MALE);

    when(contactService.updateContact(updateRequest))
        .thenThrow(
            new EmptyResultDataAccessException("No records found for the given criteria.", 1));

    String jsonRequest = objectMapper.writeValueAsString(updateRequest);

    mockMvc
        .perform(
            put(URL + "/contacts/updateContactAttributes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No records found for the given criteria."));
    verify(contactService, times(1)).updateContact(updateRequest);
  }

  @Test
  public void testDeletePhoneSuccess() throws Exception {
    Integer pin = 123456789;
    String phone = "123-456";

    when(contactService.deletePhone(pin, phone))
        .thenReturn(
            ResponseEntity.status(HttpStatus.OK).body("Phone number deleted successfully."));

    mockMvc
        .perform(
            delete(URL + "/contacts/deletePhone")
                .param("pin", pin.toString())
                .param("phone", phone))
        .andExpect(status().isOk())
        .andExpect(content().string("Phone number deleted successfully."));
  }

  @Test
  public void testDeletePhoneFailure() throws Exception {
    Integer pin = 1234;
    String phone = "555-1234";

    when(contactService.deletePhone(pin, phone))
        .thenThrow(
            new EmptyResultDataAccessException("No records found for the given criteria.", 1));

    mockMvc
        .perform(
            delete(URL + "/contacts/deletePhone")
                .param("pin", pin.toString())
                .param("phone", phone))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No records found for the given criteria."));
    verify(contactService, times(1)).deletePhone(pin, phone);
  }

  @Test
  public void testDeleteEmailSuccess() throws Exception {
    Integer pin = 123456789;
    String email = "test@gmail.com";

    when(contactService.deleteEmail(pin, email))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Email deleted successfully."));

    mockMvc
        .perform(
            delete(URL + "/contacts/deleteEmail")
                .param("pin", pin.toString())
                .param("email", email))
        .andExpect(status().isOk())
        .andExpect(content().string("Email deleted successfully."));
  }

  @Test
  public void testDeleteEmailFailure() throws Exception {
    Integer pin = 1234;
    String email = "test@gmail.com";

    when(contactService.deleteEmail(pin, email))
        .thenThrow(
            new EmptyResultDataAccessException("No records found for the given criteria.", 1));

    mockMvc
        .perform(
            delete(URL + "/contacts/deleteEmail")
                .param("pin", pin.toString())
                .param("email", email))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No records found for the given criteria."));
    verify(contactService, times(1)).deleteEmail(pin, email);
  }
}
