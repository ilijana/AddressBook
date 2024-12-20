# Address Book 

This is a Java-based address book application that allows users to manage contact information such as name, surname, gender, phone numbers, and email addresses.

The application is built with **Java 21**, **Spring Boot 3.4.0**, and **JDBC protocol** for database interaction. It uses a **PostgreSQL** database to store contact data and provides a **Swagger UI** for API documentation.

## Features

**Table 1** REST API to manage contacts

|   | **REST API** | **Path**                          | **Description**                                                                                   |
|---|--------------|-----------------------------------|---------------------------------------------------------------------------------------------------|
| 0 | **GET**      | /contacts/findContact/{pin}       | Find a contact by PIN (Personal Identification Number).                                           |
| 1 | **GET**      | /contacts/findContacts            | Retrieves all contacts, with the option to filter by name, surname, or gender.                    |
| 2 | **POST**     | /contacts/createContactFromJson   | Creates a new contact using the data provided in the JSON body.                                   |
| 3 | **POST**     | /contacts/createContact           | Creates a new contact using the data provided in the URL.                                         |
| 4 | **DELETE**   | /contacts/{pin}                   | Deletes contact by PIN (Personal Identification Number).                                          |
| 5 | **PUT**      | /contacts/updateContactAttributes | Update a specific contact attribute(single or multiple) using the data provided in the JSON body. | 
| 6 | **PUT**      | /contacts/updateContactAttribute  | Updating contact by providing attribute name and a new value for it.                              |
| 7 | **DELETE**   | /contacts/deleteEmail             | Delete the email associated with a specific contact.                                              |
| 8 | **DELETE**   | /contacts/deletePhone             | Delete the phone number associated with a specific contact.                                       |

**Table 2** Examples

| **Row in Table 1** | **Example**                                                                                                                                                                                                                               |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0                  | http://localhost:8080/api/contacts/findContact/1000      <br> @PathVariable pin                                                                                                                                                           |
| 1                  | http://localhost:8080/api/contacts/findContacts          <br> **Optional** @PathVariable name, surname or gender                                                                                                                          |
| 1                  | http://localhost:8080/api/contacts/findContacts?name=Ivan&gender=MALE   <br> **Optional** @PathVariable name, surname or gender                                                                                                           |
| 2                  | http://localhost:8080/api/contacts/createContactFromJson                <br> @RequestBody List of contacts in JSON format                                                                                                                 |
| 3                  | http://localhost:8080/api/contacts/createContact?pin=2030&name=Tanja&surname=Marić&email=tanja.maric@gmail.com      <br> @PathVariable **Mandatory** pin, name and surname **Optional** gender, email and phone                           |
| 4                  | http://localhost:8080/api/contacts/1234567891        <br> @PathVariable pin                                                                                                                                                               |
| 5                  | http://localhost:8080/api/contacts/updateContactAttributes       <br> @RequestBody one contact in JSON format - PIN is mandatory for update, rest of the attributes are optional                                                          |
| 6                  | http://localhost:8080/api/contacts/updateContactAttribute?pin=2029&attribute=email&oldValue=leo.titlic@gmail.com&newValue=leonard.titlic@gmail.com     <br> @PathVariable **Mandatory** pin, attribute and newValue **Optional** oldValue |
| 7                  | http://localhost:8080/api/contacts/deleteEmail?pin=2023&email=leo.bandic@gmail.com        <br> @PathVariable pin and email                                                                                                                |
| 8                  | http://localhost:8080/api/contacts/deletePhone?pin=2023&phone=0997899621      <br> @PathVariable pin and phone                                                                                                                            |

***JSON body***
```json
   {
      "pin": 0,
      "name": "string",
      "surname": "string",
      "gender": "MALE",
      "emails": [
         "string"
      ],
      "phones": [
         "string"
      ]
   }
```


## Prerequisites

- **Java 21** 
- **Spring Boot 3.4.0**
- **PostgreSQL** database
- **Maven** (for building the application)

## Database connection instruction

The database is hosted on Render: https://dashboard.render.com/.
To view the database content and table structure, you can connect to the database using pgAdmin.
The connection details for creating a new server in pgAdmin are provided in the table below:

| **Parameter**        | **Value**                                                                                                                                                |
|----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **hostname/address** | dpg-ctfc80jgbbvc73dd2bmg-a.frankfurt-postgres.render.com                                                                                                 |
| **port**             | 5432                                                                                                                                                     |
| **Database**         | address_book_hug2                                                                                                                                        |
| **Username**         | address_book_hug2_user                                                                                                                                   |
| **Password**         | p8yzRV9KYjJyMxzo8MiHYgMt17mcWoqC                                                                                                                         |
| **PSQL Command**     | PGPASSWORD=p8yzRV9KYjJyMxzo8MiHYgMt17mcWoqC psql -h dpg-ctfc80jgbbvc73dd2bmg-a.frankfurt-postgres.render.com -U address_book_hug2_user address_book_hug2 | 


## Starting the application from command line
1. Clone the repository:
    > git clone https://github.com/ilijana/AddressBook.git
2. The database connection details are available in the application.properties file of the AddressBook project. These details should correspond to the information provided in the table above.
3. Build and run the application:
    > mvn clean

    > mvn install

    > mvn spring-boot:run
4. The application includes Swagger UI for interactive API documentation. Once the application is running, go to: http://localhost:8080/swagger-ui/index.html
   This will show you an interactive UI where you can explore the available endpoints, see their parameters, and try out the API.

## Starting the application from docker image
1. Clone the repository:
> git clone https://github.com/ilijana/AddressBook.git
2. Navigate to the directory containing the Dockerfile:
> cd /path/to/your/project
3. Run the Docker build command:
> docker image build .
4. After the image is successfully built, you can run the container using the following command:
>  docker run -p 8080:8080 <image-hash>

   Note! <image-hash> will be written in output of step 3.

5. The application includes Swagger UI for interactive API documentation. Once the application is running, go to: http://localhost:8080/swagger-ui/index.html
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
