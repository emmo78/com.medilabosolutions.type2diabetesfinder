# com.mediLaboSolutions.type2DiabetesFinder
# microServices

The purpose of this microservices application is to help doctors find out people who can develop type 2 diabetes

## Prerequisites

### What things you need to install the software

- Java 17
- MySQL

### The things you need more to develop :

- apache Maven
- Spring Boot 3.3.5

## Installing

A step by step series of examples that tell you how to get a development env running:

1.Install Java:

https://adoptium.net/temurin/releases?version=17

2.Install Maven:

https://maven.apache.org/install.html

3.Install Spring

https://spring.io/tools
or Eclipse Marketplace
or bundled in IntelliJ ultimate

## Testing

The app has unit tests and integration tests written.

To run the tests from maven :
- go to the folder type2DiabetesFinder/ and execute the below commands :

- $ mvn clean		→ clean ./**/target
- $ mvn test		→ run Unit Tests
- $ mvn verify		→ run Unit Test and Integration Test
- \$ mvn package		→ build .jar + Jacoco report in ./\[micro-service-name\]/target/site/jacoco/index.html  
  (run : $ java -jar ./\[micro-service-name\]/target/./\[micro-service-name\]-0.0.1-SNAPSHOT.jar)
- $ mvn site 		→ put project reports in ./\[micro-service-name\]/target/site/index.html  
  ( JavaDocs, SpotBugs, Surefire & Failsafe Reports, Jacoco & JaCoCo IT Reports)
- $ mvn surefire-report:report → surefire report in	./\[micro-service-name\]/target/site/ surefire-report
- https://sonarcloud.io/

## patientService : CRUD API REST micro service for patient

root = /patientService/

### Properties : ./src/main/resources/application.properties :

Does not contain SGBD properties, these are saved in "./db.properties"
writted in "./.gitignore"  
 - spring.datasource.url
 - spring.datasource.username
 - spring.datasource.password

### Logback :
 - ./src/main/resources/logback.xml  
-> log file  = ../logs/patientService.log with RollBack
 -  ./src/main/resources/logback-test.xml  
-> log file  = ../logs/patientServiceTest.log with RollBack

### EndPoints :
GET http://localhost:9090/patients : return JSON Page of Patients  
GET http://localhost:9090/patients/{id : return JSON patient with specified id  
POST http://localhost:9090/patients/ : with JSON patient in request Body, id must be null. Return the persisted patient with id not null  
PUT http://localhost:9090/patients/ : with JSON patient in request Body, id exist in DDB. Return the persisted updated patient with same id  
DELETE http://localhost:9090/patients/{id} : deletes a patient with specified id. If the patient does not exist, the request is silently ignored

GET http://localhost:9090/v3/api-docs ; return JSON open api description  
GET http://localhost:9090/swagger-ui/index.html : return swagger ui api decription









