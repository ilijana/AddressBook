package com.example.AddressBook.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
    ErrorResponse errorResponse = new ErrorResponse("Database error", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<String> handleEmptyResult(EmptyResultDataAccessException ex) {
    return new ResponseEntity<>("No records found for the given criteria.", HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CannotGetJdbcConnectionException.class)
  public ResponseEntity<ErrorResponse> handleCannotGetJdbcConnectionException(
      CannotGetJdbcConnectionException ex) {
    ErrorResponse errorResponse = new ErrorResponse("Database connection error", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    ErrorResponse errorResponse = new ErrorResponse("Data integrity violation", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(
      java.sql.SQLIntegrityConstraintViolationException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse("Integrity constraint violation", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse("Internal server error", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<String> handleValidationExceptions(HandlerMethodValidationException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Validation failed! " + ex.getDetailMessageArguments()[0]);
  }

}
