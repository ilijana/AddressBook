package com.example.AddressBook.repository.mappers;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ContactRowMapper implements RowMapper<Contact> {

  @Override
  public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
    Contact contact = new Contact();

    contact.setPin(rs.getInt("pin"));
    contact.setName(rs.getString("name"));
    contact.setSurname(rs.getString("surname"));

    if (rs.getString("gender") != null) {
      contact.setGender(Gender.valueOf(rs.getString("gender")));
    }

    ContactPhones phones = new ContactPhones();
    if (rs.getString("phones") != null) {
      phones.add(rs.getString("phones"));
    }
    contact.setPhones(phones);

    ContactEmails emails = new ContactEmails();
    if (rs.getString("emails") != null) {
      emails.add(rs.getString("emails"));
    }
    contact.setEmails(emails);

    return contact;
  }
}
