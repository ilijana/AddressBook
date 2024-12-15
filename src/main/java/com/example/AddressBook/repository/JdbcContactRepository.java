package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.UserEmails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JdbcContactRepository implements ContactRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcContactRepository.class);
    private static final ContactRowMapper contractMapping = new ContactRowMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //SEARCH ALL - delete comment
    public List<Contact> getAllContacts() {
        String sql = "SELECT " +
                "c.pin, " +
                "c.name, " +
                "c.surname, " +
                "c.gender, " +
                "STRING_AGG(DISTINCT p.phone, ', ') AS phones, " +
                "STRING_AGG(DISTINCT e.email, ', ') AS emails " +
                "FROM contacts c " +
                "LEFT JOIN phonenumbers p ON c.pin = p.pin " +
                "LEFT JOIN emails e ON c.pin = e.pin " +
                "GROUP BY c.pin, c.name, c.surname, c.gender";

        return jdbcTemplate.query(sql, contractMapping);
    }

    //SEARCH BY PIN - delete comment
    public Contact getContactByPin(int pin) {
        String sql = "SELECT " +
                "c.pin, c.name, c.surname, c.gender, " +
                "STRING_AGG(DISTINCT pn.phone, ', ') AS phone_numbers, " +
                "STRING_AGG(DISTINCT e.email, ', ') AS emails " +
                "FROM contacts c " +
                "LEFT JOIN phonenumbers pn ON c.pin = pn.pin " +
                "LEFT JOIN emails e ON c.pin = e.pin " +
                "WHERE c.pin = ? " +
                "GROUP BY c.pin, c.name, c.surname, c.gender";

        return jdbcTemplate.queryForObject(sql, contractMapping, pin);
    }

    //SEARCH BY NAME - delete comment
    public List<Contact> searchContactsByParameter(String parameter, String searchTerm) {
        String sqlModifier;
        if (parameter.equals("gender")) {
            sqlModifier = "CAST(c.gender AS TEXT)";
        } else {
            sqlModifier = "c." + parameter;
        }
        String sql = "SELECT " +
                "c.pin, " +
                "c.name, " +
                "c.surname, " +
                "c.gender, " +
                "STRING_AGG(DISTINCT pn.phone, ', ') AS phone_numbers, " +
                "STRING_AGG(DISTINCT e.email, ', ') AS emails " +
                "FROM contacts c " +
                "LEFT JOIN phonenumbers pn ON c.pin = pn.pin " +
                "LEFT JOIN emails e ON c.pin = e.pin " +
                "WHERE " + sqlModifier + " ILIKE ? " +
                "GROUP BY c.pin, c.name, c.surname, c.gender";


        return jdbcTemplate.query(sql, contractMapping, searchTerm);
    }

    public void createContact(int pin, String name, String surname, String gender,
                              List<String> phoneNumbers, UserEmails emails) {

        // Insert into contacts table
        if (gender != null) {
            String sqlContact = "INSERT INTO contacts (pin, name, surname, gender) " +
                    "VALUES (?, ?, ?, CAST(UPPER(?) AS gender))";
            jdbcTemplate.update(sqlContact, pin, name, surname, gender);
        } else {
            String sqlContact = "INSERT INTO contacts (pin, name, surname) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlContact, pin, name, surname);
        }

        // Insert phone numbers into phonenumbers table
        String sqlPhone = "INSERT INTO phonenumbers (phone, pin) VALUES (?, ?)";
        if(phoneNumbers != null){
            for (String phone : phoneNumbers) {
                jdbcTemplate.update(sqlPhone, phone, pin);
            }
        }

        // Insert emails into emails table
        String sqlEmail = "INSERT INTO emails (email, pin) VALUES (?, ?)";
        if (emails != null){
            for (String email : emails) {
                jdbcTemplate.update(sqlEmail, email, pin);
            }
        }
    }

    // Method to delete a contact by pin
    public void deleteContactByPin(int pin) {
        String sql = "DELETE FROM contacts WHERE pin = ?";

        // Executes the delete query with the provided pin
        jdbcTemplate.update(sql, pin);
    }
}
