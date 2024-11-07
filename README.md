# com.mediLaboSolutions.type2DiabetesFinder
# microServices

The purpose of this microservices application is to help doctors find out people who can develop type 2 diabetes

## Prerequisites

### What things you need to install the software

- Java 17
- apache Maven
- Spring Boot 3.3.5

### Properties : ./src/main/resources/application.properties :

Does not contain SGBD properties : 
 - spring.datasource.url
 - spring.datasource.username
 - spring.datasource.password

These are saved in "./db.properties" writted in "./.gitignore"

### Log4J2 : ./src/main/resources/log4j2-spring.xml
log file  = ./logs/PayMyBuddy-log4j2.log with RollBack

## Installing

A step by step series of examples that tell you how to get a development env running:

1.Install Java:

https://adoptium.net/temurin/releases?version=17

2.Install Maven:

https://maven.apache.org/install.html

3.Install Spring

https://spring.io/tools
or Eclipse Marketplace

## Testing

The app has unit tests and integration tests written.

To run the tests from maven, go to the folder that contains the pom.xml file and execute the below commands :

- $ mvn clean		→ clean ./target
- $ mvn test		→ run Unit Tests
- $ mvn verify		→ run Unit Tests, SIT and AIT
- $ mvn package		→ build .jar + Jacoco report in ./target/site/jacoco/index.html  
					(run : $ java -jar target/paymybuddy-0.0.1-SNAPSHOT.jar)
- $ mvn site 		→ put project reports in ./target/site/index.html  
					( JavaDocs, SpotBugs, Surefire & Failsafe Reports, Jacoco & JaCoCo IT Reports)
- $ mvn surefire-report:report → surefire report in	./target/site/ surefire-report
- https://sonarcloud.io/summary/overall?id=emmo78_Poseidon






