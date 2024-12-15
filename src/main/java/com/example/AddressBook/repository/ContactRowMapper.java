package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.UserEmails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class ContactRowMapper implements RowMapper<Contact> {

    @Override
    public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contact contact = new Contact();
        contact.setPin(rs.getInt("pin"));
        contact.setName(rs.getString("name"));
        contact.setSurname(rs.getString("surname"));
        contact.setGender(rs.getString("gender"));

        // Set the phones (assuming there's only one phone per row)
        contact.setPhones(Collections.singletonList(rs.getString("phone_numbers")));

        // Set the emails (assuming there's only one email per row)
        UserEmails emails = new UserEmails();
        emails.add(rs.getString("emails"));
        contact.setEmails(emails);

        return contact;
    }
}