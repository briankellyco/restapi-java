# restapi-java 

![stability-badge](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

A REST API showcasing a modern Spring Boot / Java 21 microservice. Key features include:

A minimalistic exception handling strategy:

* I designed the concept of explicitly setting the REST response code in the message.
* The code in message approach requires you as a developer to think about the response code for every exception.
* The design allows you to centralise all application error codes and descriptions in one place.
* The simplicity of the design reduces the amount of code required to handle exceptions.

And:

* A set of best practice design decisions applied throughout the codebase.

## Run the application
1.  Checkout the github repo, import into your IDE and run using Java 21. Instructions for Intellij are below.
2.  Verify the test dataset that the application comes with is loaded by inspecting the database.
    The database is accessible at http://localhost:8080/h2-ui 
3.  Call the REST API using curl or Postman:

    * Postman collection is available at /docs/restapi-java.postman_collection.json
    
    Curl examples:

    * curl -X GET --header "Content-type: application/json" --header "Accept: application/json" http://localhost:8080/charge-sessions?vehicleId=10&sort=endTime
    * curl -X GET --header "Content-type: application/json" --header "Accept: application/json" http://localhost:8080/charge-sessions/20
    * curl -X POST --header "Content-type: application/json" --header "Accept: application/json"  --data '{"vehicleId":10, "chargePointId":1}' http://localhost:8080/charge-sessions
    * curl -X PUT --header "Content-type: application/json" --header "Accept: application/json"  --data '{}' http://localhost:8080/charge-sessions/23

## Technologies used to build the API
``` 
Spring boot project init:
    https://start.spring.io/
Choosing:
    Spring 3.5.0
    Java 21
    Gradle 8.12+
Adding dependencies:
    Lombok
    Spring Web
    Spring Data JPA
    Liquibase Migration
    H2 Database (for local development and in-memory deployment to cloud)
    Testcontainers
    PosgreSQL Database (for integration tests)
```

## Intellij setup to run the application
``` 
Intellij project using JDK 21+. Do following to avoid compilation issues:
    1.  Create new empty intellij project.
    2.  Import restapi-java module into project.
    3.  Configure JDK 21+ for the project (see prompt when you open a java file).
    4.  Enable Lombok plugin in Intellij. Follow guide https://www.baeldung.com/lombok-ide
    4.  Run Spring application. 
    
    Additionally if gradle reports following error:
        "No matching variant of org.springframework.boot:spring-boot-gradle-plugin:3.5.0 was found"
    Configure gradle to use JDK 21+ in Intellij:
        File > Settings >   Build, Execution, Deployment > Build Tools > Gradle > Select project > Set Gradle JVM to 21+
    Refresh project dependencies and run application.
```


## What is the use case for this microservice?

The use case is create a generic REST api microservice that can be used to model a standard business problem. 

All business problems involve some sort of 1:M relationship or M:N relationship.

For example:
* a factory produces many cars and wants to track the time taken at each stage of the production line
* a farmer has many sheep in his field and wants to track the health of each sheep

The business case for this microservice is:

* I'm a driver (with a vehicle) and I want to track the cost of charging my vehicle (session) at a charging point. 

REST endpoints to provide are:

1. Create a charging record.
2. Get a charging record by id.
3. Find all charging records for given vehicle.

## Author

[Brian Kelly](https://github.com/briankellyco)


## Support my work

If you found this REST API Java example helpful, please consider showing your support by buying me a coffee.

<a href="https://www.buymeacoffee.com/briankellyco" target="_blank"><img src="docs/coffee.png"></a>