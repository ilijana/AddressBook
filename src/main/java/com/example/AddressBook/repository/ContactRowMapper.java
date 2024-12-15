package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import com.example.AddressBook.model.ContactPhones;
import org.springframework.jdbc.core.RowMapper;

public class ContactRowMapper implements RowMapper<Contact> {

  @Override
  public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
    Contact contact = new Contact();
    contact.setPin(rs.getInt("pin"));
    contact.setName(rs.getString("name"));
    contact.setSurname(rs.getString("surname"));
    contact.setGender(rs.getString("gender"));

    ContactPhones phones = new ContactPhones();
    phones.add(rs.getString("phones"));
    contact.setPhones(phones);

    ContactEmails emails = new ContactEmails();
    emails.add(rs.getString("emails"));
    contact.setEmails(emails);

    return contact;
  }
}
