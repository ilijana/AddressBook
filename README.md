# Address Book 

This is a Java-based address book application that allows users to manage contact information such as name, surname, gender, phone numbers, and email addresses.

The application is built with **Java 21**, **Spring Boot 3.4.0**, and **JDBC Template** for database interaction. It uses a **PostgreSQL** database to store contact data and provides a **Swagger UI** for API documentation.

## Features

**REST API** to manage contacts:

| **REST API** | **Path**                          | **Description**                                                                                   |
|--------------|-----------------------------------|---------------------------------------------------------------------------------------------------|
| **GET**      | /contacts/findContact/{pin}       | Find a contact by PIN (Personal Identification Number).                                           |
| **GET**      | /contacts/findContacts            | Retrieves all contacts, with the option to filter by name, surname, or gender.                    |
| **POST**     | /contacts/createContactFromJson   | Creates a new contact using the data provided in the JSON body.                                   |
| **POST**     | /contacts/createContact           | Creates a new contact using the data provided in the URL.                                         |
| **DELETE**   | /contacts/{pin}                   | Deletes contact by PIN (Personal Identification Number).                                          |
| **PUT**      | /contacts/updateContactAttributes | Update a specific contact attribute(single or multiple) using the data provided in the JSON body. | 
| **PUT**      | /contacts/updateContactAttribute  | Updating contact by providing attribute name and a new value for it.                              |
| **DELETE**   | /contacts/deleteEmail             | Delete the email associated with a specific contact.                                              |
| **DELETE**   | /contacts/deletePhone             | Delete the phone number associated with a specific contact.                                       |

## Prerequisites

- **Java 21** 
- **Spring Boot 3.4.0**
- **PostgreSQL** database
- **Maven** (for building the application)

## Database connection instruction

The database is hosted on Render: https://dashboard.render.com/.
To view the database content and table structure, you can connect to the database using pgAdmin.
The connection details for creating a new server in pgAdmin are provided in the table below:

| **Parameter**        | **Value**                                                |
|----------------------|----------------------------------------------------------|
| **hostname/address** | dpg-ctfc80jgbbvc73dd2bmg-a.frankfurt-postgres.render.com |
| **port**             | 5432                                                     |
| **Database**         | address_book_hug2                                        |
| **Username**         | address_book_hug2_user                                   |
| **Password**         | p8yzRV9KYjJyMxzo8MiHYgMt17mcWoqC                         |

## Starting the application
1. Clone the repository:
    > git clone https://github.com/ilijana/AddressBook.git
2. The database connection details are available in the application.properties file of the AddressBook project. These details should correspond to the information provided in the table above.
3. Build and run the application:
    > mvn clean

    > mvn install

    > mvn spring-boot:run
4. The application includes Swagger UI for interactive API documentation. Once the application is running, go to: http://localhost:8080/swagger-ui/index.html
   This will show you an interactive UI where you can explore the available endpoints, see their parameters, and try out the API.


## Database Structure

The database has three tables:

1. **Contact Table**: Stores basic contact details.
    - Columns: `pin`, `name`, `surname`, `gender`

2. **Phones Table**: Stores phone numbers associated with each contact.
    - Columns: `phone_id`, `phone`, `pin` (foreign key referencing `pin` in the `Contact` table)

3. **Emails Table**: Stores email addresses associated with each contact.
    - Columns: `email_id`, `email`, `pin` (foreign key referencing `pin` in the `Contact` table)

### Schema for PostgreSQL:
```sql
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE', 'OTHER');

CREATE TABLE contacts (
    pin  INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    gender gender
);

CREATE TABLE emails (
    email_id SERIAL PRIMARY KEY,               
    email VARCHAR(255) NOT NULL,               
    pin INT NOT NULL,                          
    CONSTRAINT fk_pin FOREIGN KEY (pin)      
        REFERENCES contacts (pin)            
        ON DELETE CASCADE,                   
    CONSTRAINT email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')  
);

CREATE TABLE phones (
    phone_id SERIAL PRIMARY KEY,         
    phone VARCHAR(20) NOT NULL,           
    pin INT NOT NULL,                     
    CONSTRAINT fk_pin FOREIGN KEY (pin) 
        REFERENCES contacts (pin)        
        ON DELETE CASCADE                
);

ALTER TABLE phones
ADD CONSTRAINT phone_number_valid
CHECK (phone ~ '^\+?[0-9]{1,4}?[-. \(\)]?(\d{1,3})?[-. \(\)]?\d{1,3}[-. \(\)]?\d{4}$');
