package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.repository.mappers.ContactRowMapper;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcContactRepository implements ContactRepository {

  private static final Logger log = LoggerFactory.getLogger(JdbcContactRepository.class);
  private static final ContactRowMapper contractMapping = new ContactRowMapper();

  @Autowired private JdbcTemplate jdbcTemplate;

  // Search for all contacts, each contact is printed in one row
  public List<Contact> getAllContacts() {
    String sql =
        "SELECT "
            + "c.pin, "
            + "c.name, "
            + "c.surname, "
            + "c.gender, "
            + "STRING_AGG(DISTINCT p.phone, ', ') AS phones, "
            + "STRING_AGG(DISTINCT e.email, ', ') AS emails "
            + "FROM contacts c "
            + "LEFT JOIN phonenumbers p ON c.pin = p.pin "
            + "LEFT JOIN emails e ON c.pin = e.pin "
            + "GROUP BY c.pin, c.name, c.surname, c.gender";

    return jdbcTemplate.query(sql, contractMapping);
  }

  // Search by pin, print contact in one row
  public Contact getContactByPin(int pin) {
    String sql =
        "SELECT "
            + "c.pin, c.name, c.surname, c.gender, "
            + "STRING_AGG(DISTINCT pn.phone, ', ') AS phones, "
            + "STRING_AGG(DISTINCT e.email, ', ') AS emails "
            + "FROM contacts c "
            + "LEFT JOIN phonenumbers pn ON c.pin = pn.pin "
            + "LEFT JOIN emails e ON c.pin = e.pin "
            + "WHERE c.pin = ? "
            + "GROUP BY c.pin, c.name, c.surname, c.gender";

    return jdbcTemplate.queryForObject(sql, contractMapping, pin);
  }

  // Search contact by parameters name, surname and gender. Using multiple parameters is allowed.
  public List<Contact> searchContactsByParameter(
      String searchName, String searchSurname, String searchGender) {
    String sqlMultipleSearch =
        formSqlQueryForMultipleParam(searchName, searchSurname, searchGender);
    List<Object> providedParams = getProvidedRequestParams(searchName, searchSurname, searchGender);

    String sql =
        "SELECT "
            + "c.pin, "
            + "c.name, "
            + "c.surname, "
            + "c.gender, "
            + "STRING_AGG(DISTINCT pn.phone, ', ') AS phones, "
            + "STRING_AGG(DISTINCT e.email, ', ') AS emails "
            + "FROM contacts c "
            + "LEFT JOIN phonenumbers pn ON c.pin = pn.pin "
            + "LEFT JOIN emails e ON c.pin = e.pin "
            + "WHERE "
            + sqlMultipleSearch
            + " GROUP BY c.pin, c.name, c.surname, c.gender";

    return jdbcTemplate.query(sql, contractMapping, providedParams.toArray());
  }

  // Create new contact.
  public void createContact(
      int pin,
      String name,
      String surname,
      String gender,
      ContactPhones phoneNumbers,
      ContactEmails emails) {

    // Insert into contacts table
    if (gender != null) {
      String sqlContact =
          "INSERT INTO contacts (pin, name, surname, gender) "
              + "VALUES (?, ?, ?, CAST(UPPER(?) AS gender))";
      jdbcTemplate.update(sqlContact, pin, name, surname, gender);
    } else {
      String sqlContact = "INSERT INTO contacts (pin, name, surname) VALUES (?, ?, ?)";
      jdbcTemplate.update(sqlContact, pin, name, surname);
    }

    // Insert phone numbers into phonenumbers table
    String sqlPhone = "INSERT INTO phonenumbers (phone, pin) VALUES (?, ?)";
    if (phoneNumbers != null) {
      for (String phone : phoneNumbers) {
        jdbcTemplate.update(sqlPhone, phone, pin);
      }
    }

    // Insert emails into emails table
    String sqlEmail = "INSERT INTO emails (email, pin) VALUES (?, ?)";
    if (emails != null) {
      for (String email : emails) {
        jdbcTemplate.update(sqlEmail, email, pin);
      }
    }
  }

  // Method to delete a contact by pin.
  public void deleteContactByPin(int pin) {
    String sql = "DELETE FROM contacts WHERE pin = ?";
    jdbcTemplate.update(sql, pin);
  }

  // Main method to update contact details.
  @Transactional
  public void updateContactDetails(Contact updatedContact) {
    updateContact(
        updatedContact.getPin(),
        updatedContact.getName(),
        updatedContact.getSurname(),
        updatedContact.getGender());
    updateEmails(updatedContact.getPin(), updatedContact.getEmails());
    updatePhoneNumbers(updatedContact.getPin(), updatedContact.getPhones());
  }

  // Update contact details (name, surname, gender).
  public void updateContact(int pin, String name, String surname, String gender) {
    String sql =
        "UPDATE contacts SET name = ?, surname = ?, gender = CAST(UPPER(?) AS gender) WHERE pin = ?";
    jdbcTemplate.update(sql, name, surname, gender, pin);
  }

  // Update emails for a given contact (delete old, insert new ones).
  public void updateEmails(int pin, List<String> emails) {
    // Delete old emails
    String deleteSql = "DELETE FROM emails WHERE pin = ?";
    jdbcTemplate.update(deleteSql, pin);

    // Insert new emails
    String insertSql = "INSERT INTO emails (pin, email) VALUES (?, ?)";
    for (String email : emails) {
      jdbcTemplate.update(insertSql, pin, email);
    }
  }

  // Update phone numbers for a given contact (delete old, insert new ones).
  public void updatePhoneNumbers(int pin, List<String> phones) {
    // Delete old phone numbers
    String deleteSql = "DELETE FROM phonenumbers WHERE pin = ?";
    jdbcTemplate.update(deleteSql, pin);

    // Insert new phone numbers
    String insertSql = "INSERT INTO phonenumbers (pin, phone) VALUES (?, ?)";
    log.info("Phones " + phones);
    for (String phone : phones) {
      jdbcTemplate.update(insertSql, pin, phone);
    }
  }

  public void deleteEmail(int pin, String email) {
    try {
      // Step 1: Check if the email exists for the given pin
      String findEmailQuery = "SELECT email_id FROM emails WHERE email = ? AND pin = ?";
      Integer emailId = jdbcTemplate.queryForObject(findEmailQuery, Integer.class, email, pin);

      if (emailId != null) {
        // Step 2: Delete the email for the specific pin
        String deleteEmailQuery = "DELETE FROM emails WHERE email_id = ?";
        jdbcTemplate.update(deleteEmailQuery, emailId);
        log.info("Email deleted successfully for Pin: {}", pin);
      } else {
        log.info("No email found for the specified Pin: {}", pin);
      }
    } catch (DataAccessException e) {
      log.info("An error occurred while trying to delete the email: {}", String.valueOf(e));
    }
  }

  public void deletePhone(int pin, String phone) {
    try {
      // Step 1: Check if the phone exists for the given pin
      String findPhoneQuery = "SELECT phone_id FROM phonenumbers WHERE phone = ? AND pin = ?";
      Integer phoneId = jdbcTemplate.queryForObject(findPhoneQuery, Integer.class, phone, pin);

      if (phoneId != null) {
        // Step 2: Delete the phone for the specific pin
        String deletePhoneQuery = "DELETE FROM phonenumbers WHERE phone_id = ?";
        jdbcTemplate.update(deletePhoneQuery, phoneId);
        log.info("Phone deleted successfully for Pin: {}", pin);
      } else {
        log.info("No phone found for the specified Pin: {}", pin);
      }
    } catch (DataAccessException e) {
      log.info("An error occurred while trying to delete the phone: {}", String.valueOf(e));
    }
  }

  private List<Object> getProvidedRequestParams(
      String searchName, String searchSurname, String searchGender) {
    List<Object> params = new ArrayList<>();
    if (searchName != null) {
      params.add(searchName);
    }
    if (searchSurname != null) {
      params.add(searchSurname);
    }
    if (searchGender != null) {
      params.add(searchGender);
    }
    return params;
  }

  private String formSqlQueryForMultipleParam(
      String searchName, String searchSurname, String searchGender) {
    StringBuilder sql = new StringBuilder();

    if (searchName != null) {
      sql.append("c.name ILIKE ? ");
    }
    if (searchSurname != null) {
      if (!sql.isEmpty()) {
        sql.append("AND ");
      }
      sql.append("c.surname ILIKE ? ");
    }
    if (searchGender != null) {
      if (!sql.isEmpty()) {
        sql.append("AND ");
      }
      sql.append("CAST(c.gender AS TEXT) ILIKE ? ");
    }
    return sql.toString().trim();
  }
}
