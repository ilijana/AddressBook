package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import com.example.AddressBook.model.ContactEmails;
import com.example.AddressBook.model.ContactPhones;
import com.example.AddressBook.model.Gender;
import com.example.AddressBook.repository.mappers.ContactRowMapper;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            + "LEFT JOIN phones p ON c.pin = p.pin "
            + "LEFT JOIN emails e ON c.pin = e.pin "
            + "GROUP BY c.pin, c.name, c.surname, c.gender";
    log.info(sql);
    return jdbcTemplate.query(sql, contractMapping);
  }

  // Search by pin, print contact in one row
  public Contact getContactByPin(Integer pin) {
    String sql =
        "SELECT "
            + "c.pin, c.name, c.surname, c.gender, "
            + "STRING_AGG(DISTINCT pn.phone, ', ') AS phones, "
            + "STRING_AGG(DISTINCT e.email, ', ') AS emails "
            + "FROM contacts c "
            + "LEFT JOIN phones pn ON c.pin = pn.pin "
            + "LEFT JOIN emails e ON c.pin = e.pin "
            + "WHERE c.pin = ? "
            + "GROUP BY c.pin, c.name, c.surname, c.gender";

    return jdbcTemplate.queryForObject(sql, contractMapping, pin);
  }

  // Search contact by parameters name, surname and gender. Using multiple parameters is allowed.
  public List<Contact> searchContactsByParameter(
      String searchName, String searchSurname, Gender searchGender) {
    String sqlMultipleSearch =
        formSqlQueryForMultipleParam(searchName, searchSurname, searchGender);
    log.info(sqlMultipleSearch);
    List<Object> providedParams = getProvidedRequestParams(searchName, searchSurname, searchGender);
    log.info(providedParams.toString());
    String sql =
        "SELECT "
            + "c.pin, "
            + "c.name, "
            + "c.surname, "
            + "c.gender, "
            + "STRING_AGG(DISTINCT pn.phone, ', ') AS phones, "
            + "STRING_AGG(DISTINCT e.email, ', ') AS emails "
            + "FROM contacts c "
            + "LEFT JOIN phones pn ON c.pin = pn.pin "
            + "LEFT JOIN emails e ON c.pin = e.pin "
            + "WHERE "
            + sqlMultipleSearch
            + " GROUP BY c.pin, c.name, c.surname, c.gender";

    return jdbcTemplate.query(sql, contractMapping, providedParams.toArray());
  }

  @Transactional
  public void contactCreation(
      Integer pin,
      String name,
      String surname,
      Gender gender,
      ContactPhones phones,
      ContactEmails emails) {
    createContact(pin, name, surname, gender);
    createEmails(pin, emails);
    createPhones(pin, phones);
  }

  public void createContact(Integer pin, String name, String surname, Gender gender) {
    if (gender != null) {
      String sqlContact =
          "INSERT INTO contacts (pin, name, surname, gender) "
              + "VALUES (?, ?, ?, CAST(? AS gender))";
      jdbcTemplate.update(sqlContact, pin, name, surname, gender.toString());
    } else {
      String sqlContact = "INSERT INTO contacts (pin, name, surname) VALUES (?, ?, ?)";
      jdbcTemplate.update(sqlContact, pin, name, surname);
    }
  }

  public void createPhones(Integer pin, ContactPhones phones) {
    String sqlPhone = "INSERT INTO phones (phone, pin) VALUES (?, ?)";
    if (phones != null) {
      for (String phone : phones) {
        jdbcTemplate.update(sqlPhone, phone, pin);
      }
    }
  }

  public void createEmails(Integer pin, ContactEmails emails) {
    String sqlEmail = "INSERT INTO emails (email, pin) VALUES (?, ?)";
    if (emails != null) {
      for (String email : emails) {
        jdbcTemplate.update(sqlEmail, email, pin);
      }
    }
  }

  public String deleteContactByPin(Integer pin) {
    String sql = "DELETE FROM contacts WHERE pin = ?";
    // Perform the update (deletion) and get the number of affected rows
    int rowsAffected = jdbcTemplate.update(sql, pin);

    if (rowsAffected == 0) {
      return "No contact found with PIN: " + pin;
    } else {
      return "Contact with PIN " + pin + " deleted successfully.";
    }
  }

  @Transactional
  public void updateContactDetails(Contact updatedContact) {
    updateContact(
        updatedContact.getPin(),
        updatedContact.getName(),
        updatedContact.getSurname(),
        updatedContact.getGender());
    updateEmails(updatedContact.getPin(), updatedContact.getEmails());
    updatePhones(updatedContact.getPin(), updatedContact.getPhones());
  }

  public void updateContact(Integer pin, String name, String surname, Gender gender) {
    String sql =
        "UPDATE contacts SET name = ?, surname = ?, gender = CAST(UPPER(?) AS gender) WHERE pin = ?";
    jdbcTemplate.update(sql, name, surname, gender.toString(), pin);
  }

  public void updateEmails(Integer pin, List<String> emails) {
    String deleteSql = "DELETE FROM emails WHERE pin = ?";
    jdbcTemplate.update(deleteSql, pin);

    String insertSql = "INSERT INTO emails (pin, email) VALUES (?, ?)";
    for (String email : emails) {
      jdbcTemplate.update(insertSql, pin, email);
    }
  }

  public void updatePhones(Integer pin, List<String> phones) {
    String deleteSql = "DELETE FROM phones WHERE pin = ?";
    jdbcTemplate.update(deleteSql, pin);

    String insertSql = "INSERT INTO phones (pin, phone) VALUES (?, ?)";
    for (String phone : phones) {
      jdbcTemplate.update(insertSql, pin, phone);
    }
  }

  public void deleteEmail(Integer pin, String email) {
    String findEmailQuery = "SELECT email_id FROM emails WHERE email = ? AND pin = ?";
    Integer emailId = jdbcTemplate.queryForObject(findEmailQuery, Integer.class, email, pin);

    if (emailId != null) {
      String deleteEmailQuery = "DELETE FROM emails WHERE email_id = ?";
      jdbcTemplate.update(deleteEmailQuery, emailId);
      log.info("Email deleted successfully for Pin: {}", pin);
    } else {
      log.info("No email found for the specified Pin: {}", pin);
    }
  }

  public void deletePhone(Integer pin, String phone) {
    String findPhoneQuery = "SELECT phone_id FROM phones WHERE phone = ? AND pin = ?";
    Integer phoneId = jdbcTemplate.queryForObject(findPhoneQuery, Integer.class, phone, pin);

    if (phoneId != null) {
      String deletePhoneQuery = "DELETE FROM phones WHERE phone_id = ?";
      jdbcTemplate.update(deletePhoneQuery, phoneId);
      log.info("Phone deleted successfully for Pin: {}", pin);
    } else {
      log.info("No phone found for the specified Pin: {}", pin);
    }
  }

  private List<Object> getProvidedRequestParams(
      String searchName, String searchSurname, Gender searchGender) {
    List<Object> params = new ArrayList<>();
    if (searchName != null) {
      params.add(searchName);
    }
    if (searchSurname != null) {
      params.add(searchSurname);
    }
    if (searchGender != null) {
      params.add(searchGender.toString());
    }
    return params;
  }

  private String formSqlQueryForMultipleParam(
      String searchName, String searchSurname, Gender searchGender) {
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
      sql.append("CAST(c.gender AS TEXT) = ? ");
    }
    return sql.toString().trim();
  }
}
