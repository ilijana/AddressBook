package com.example.AddressBook.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {

  @ExceptionHandler(DataAccessException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
    ErrorResponse errorResponse = new ErrorResponse("Database error", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<String> handleEmptyResult(EmptyResultDataAccessException ex) {
    // Return a specific message for when no rows are found
    return new ResponseEntity<>("No records found for the given criteria.", HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CannotGetJdbcConnectionException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public ResponseEntity<ErrorResponse> handleCannotGetJdbcConnectionException(
      CannotGetJdbcConnectionException ex) {
    ErrorResponse errorResponse = new ErrorResponse("Database connection error", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    ErrorResponse errorResponse = new ErrorResponse("Data integrity violation", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(
      java.sql.SQLIntegrityConstraintViolationException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse("Integrity constraint violation", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse("Internal server error", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
