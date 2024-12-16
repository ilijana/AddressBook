package com.example.AddressBook.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
  private String errorType;
  private String errorMessage;

  // Constructor
  public ErrorResponse(String errorType, String errorMessage) {
    this.errorType = errorType;
    this.errorMessage = errorMessage;
  }
}
