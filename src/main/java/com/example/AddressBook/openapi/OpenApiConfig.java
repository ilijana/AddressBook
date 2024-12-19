package com.example.AddressBook.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Address Book API Documentation")
                .description(
                    "Comprehensive documentation of the REST API for managing contacts in the Address Book application. Explore all available endpoints and their functionality."));
  }
}
