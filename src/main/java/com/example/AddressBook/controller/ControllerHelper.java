package com.example.AddressBook.controller;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.Gender;
import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerHelper {
  public Contact updateContact(Contact currentContact, Contact updateRequest) {
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

  boolean isInvalidGender(String gender) {
    try {
      Gender.valueOf(gender.toUpperCase());
      return false;
    } catch (IllegalArgumentException e) {
      log.warn("Provided gender is not valid! Valid genders:{}", Arrays.toString(Gender.values()));
      return true;
    }
  }

  boolean checkIfRequestParamIsSet(String name, String surname, String gender) {
    return StringUtils.isNotBlank(name)
        || StringUtils.isNotBlank(surname)
        || StringUtils.isNotBlank(gender);
  }
}
