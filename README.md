# restapi-java 

![stability-badge](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

A REST API showcasing a modern Spring Boot / Java 17 microservice notable for:

1.  An innovative minimalistic exception handling strategy:

* I designed the concept of explicitly setting the REST response code in the message.
* The code in message approach forces you as a developer to consider what the consumer of your API receives.
* I believe teams will love the simplicity at a technical level and the focus on the API consumer.

2.  And not forgetting:

* A set of best practice design decisions applied throughout the codebase.
* For brevity, I don't detail the design decisions here. I am however always pleased to discuss in person.

## Run the application
1.  Checkout the github repo, import into your IDE and run using Java 17. Instructions for Intellij are below.
2.  Verify the test dataset that the application comes with is loaded by inspecting the database.
    The database is accessible at http://localhost:8080/h2-console. 
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
    Spring 3.1.3
    Java 17
    Gradle 7.3+
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
Intellij project using JDK 17+. Do following to avoid compilation issues:
    1.  Create new empty intellij project.
    2.  Import restapi-java module into project.
    3.  Configure JDK 17+ for the project (see prompt when you open a java file).
    4.  Enable Lombok plugin in Intellij. Follow guide https://www.baeldung.com/lombok-ide
    4.  Run Spring application. 
    
    Additionally if gradle reports following error:
        "No matching variant of org.springframework.boot:spring-boot-gradle-plugin:3.1.3 was found"
    Configure gradle to use JDK 17+ in Intellij:
        File > Settings >   Build, Execution, Deployment > Build Tools > Gradle > Select project > Set Gradle JVM to 17+
    Refresh project dependencies and run application.
```


## What is the use case for this microservice?

The use case is an example of a generic 1:M relationship between different entities stored in a database. 

Almost all business problems can be modelled as a 1:M relationship between different entities.

The use case chosen for this API example is:

* I'm a car driver (with a vehicle) and I want to charge my vehicle (charging session) at a charging point.
* I want to record each charging session and the associated cost.
* I want to provide an API that enables create, read, update and delete (CRUD) on the data. 

REST endpoints provided are:

1. Create charging record.
2. Get a charging record by id.
3. Find all charging records for given vehicle (where "start time" and "end time" fields must be sortable).

## Author

[Brian Kelly](https://github.com/briankellyco)


## Support my work

If you found this REST API Java example helpful, please consider showing your support by buying me a coffee.

<a href="https://www.buymeacoffee.com/briankellyco" target="_blank"><img src="https://img.buymeacoffee.com/button-api/?text=Buy me a coffee&emoji=&slug=briankellyco&button_colour=FFDD00&font_colour=000000&font_family=Cookie&outline_colour=000000&coffee_colour=ffffff"></a>