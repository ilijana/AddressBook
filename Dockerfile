FROM maven:latest as builder

WORKDIR /usr/src/app

COPY src/ ./src/
COPY pom.xml .

RUN mvn clean package

FROM openjdk:21-jdk as runtime

WORKDIR /usr/src/app
COPY --from=builder /usr/src/app/target/*.jar /usr/src/app/AddressBook.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "AddressBook.jar"]