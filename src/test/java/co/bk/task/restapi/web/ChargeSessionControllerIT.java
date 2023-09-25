package co.bk.task.restapi.web;

import co.bk.task.restapi.model.ChargePoint;
import co.bk.task.restapi.model.ChargeSession;
import co.bk.task.restapi.model.Vehicle;
import co.bk.task.restapi.repository.ChargePointRepository;
import co.bk.task.restapi.repository.ChargeSessionListRepository;
import co.bk.task.restapi.repository.VehicleRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = DEFINED_PORT, properties = {
        "spring.liquibase.enabled=false"
})
@Testcontainers          // activate automatic startup and stop of containers
@ActiveProfiles("itest") // integration test specific profile
public class ChargeSessionControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:15.3"));

    @Bean
    @Profile("itest")
    public DataSource dataSource() {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUrl(postgreSQLContainer.getJdbcUrl());
        datasource.setUsername(postgreSQLContainer.getUsername());
        datasource.setPassword(postgreSQLContainer.getPassword());
        return datasource;
    }

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ChargePointRepository chargePointRepository;

    @Autowired
    private ChargeSessionListRepository chargeSessionListRepository;

    @Test
    void get_chargeSesssions_for_vehicleId_success() {

        List<Vehicle> vehicleList = vehicleRepository.saveAll(List.of(
                Vehicle.createVehicle("20-WW-11227"),
                Vehicle.createVehicle("23-D-45893")
        ));

        List<ChargePoint> chargePointList = chargePointRepository.saveAll(List.of(
                ChargePoint.createChargePoint("charger-model-x123", 50.0),
                ChargePoint.createChargePoint("charger-model-PP234", 7.0)
        ));

        Vehicle vehicleWithChargeSession = vehicleList.get(0);

        chargeSessionListRepository.save(ChargeSession.createChargeSession(vehicleWithChargeSession, chargePointList.get(0), new BigDecimal("1.55"), System.currentTimeMillis()));

        /*
         * Example response:
         *   [{"id":5,"sessionId":"90761149-6411-48bd-a6f7-c5d8b1c4c61d","startTime":1695578547491,"endTime":1695578547491,"totalCost":1.55000000,"vehicleId":1,"chargePointId":3}]
         */
        Response response = when().get(String.format("/charge-sessions?vehicleId=%s&sort=endTime", vehicleWithChargeSession.getId().toString()));

        long vehicleId = response.then().assertThat()
                .statusCode(200)
                .body("[0].totalCost", notNullValue())
                .extract()
                .jsonPath()
                .getLong("[0].vehicleId");

        assertThat(vehicleId, equalTo(vehicleWithChargeSession.getId()));
    }

    @Test
    void get_chargeSesssions_for_vehicleId_fail_not_found() {

        String vehicleId = "1234";

        Response response = when().get(String.format("/charge-sessions?vehicleId=%s&sort=endTime", vehicleId));

        response.then().assertThat()
                .statusCode(404)
                .body("application_code", is("RESTAPI-0002"));
    }

    @Test
    void post_create_chargeSesssion_success() {

        List<Vehicle> vehicleList = vehicleRepository.saveAll(List.of(
                Vehicle.createVehicle("20-WW-11227"),
                Vehicle.createVehicle("23-D-45893")
        ));

        List<ChargePoint> chargePointList = chargePointRepository.saveAll(List.of(
                ChargePoint.createChargePoint("charger-model-x123", 50.0),
                ChargePoint.createChargePoint("charger-model-PP234", 7.0)
        ));

        Vehicle vehicle = vehicleList.get(0);
        ChargePoint chargePoint = chargePointList.get(0);

        String requestBody = "{"
                + "\"vehicleId\": \"" + vehicle.getId().toString() + "\","
                + "\"chargePointId\": \"" + chargePoint.getId().toString() + "\""
                + "}";

        String locationHeader = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/charge-sessions")
                .then()
                .statusCode(201)
                .header("Location", not(isEmptyOrNullString()))
                .extract()
                .header("Location");

        System.out.println("Created object ID: " + locationHeader);
    }

    @Test
    void post_create_chargeSesssion_fail_missing_id() {

        String requestBody = "{"
                + "\"vehicleId\": \"22\","
                + "\"chargePointId\": \"\""
                + "}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/charge-sessions");

        response.then().assertThat()
                .statusCode(400)
                .body("application_code", is("RESTAPI-0001"));
    }

    @Test
    void put_chargeSesssion_success() {

        List<Vehicle> vehicleList = vehicleRepository.saveAll(List.of(
                Vehicle.createVehicle("20-WW-11227"),
                Vehicle.createVehicle("23-D-45893")
        ));

        List<ChargePoint> chargePointList = chargePointRepository.saveAll(List.of(
                ChargePoint.createChargePoint("charger-model-x123", 50.0),
                ChargePoint.createChargePoint("charger-model-PP234", 7.0)
        ));

        ChargeSession chargeSession = chargeSessionListRepository.save(ChargeSession.createChargeSession(
                vehicleList.get(0), chargePointList.get(0), new BigDecimal("1.55"), System.currentTimeMillis())
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put(String.format("/charge-sessions/%s", chargeSession.getId().toString()))
                .then()
                .statusCode(204);
    }



}
