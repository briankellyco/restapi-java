CREATE TABLE PUBLIC.CHARGE_POINT (
     ID BIGINT PRIMARY KEY NOT NULL,
     manufacturer_model VARCHAR(50),
     charging_power_kw NUMERIC(8,2),
     lat VARCHAR(50),
     lon VARCHAR(50),
     status VARCHAR(20),
     date_created BIGINT,
     date_updated BIGINT
);

CREATE TABLE PUBLIC.VEHICLE (
    id BIGINT PRIMARY KEY NOT NULL,
    license_plate VARCHAR(50),
    status VARCHAR(20),
    battery_capacity_kwh NUMERIC(8, 2),
    battery_level_percent NUMERIC(5, 2),
    date_created BIGINT,
    date_updated BIGINT
);

CREATE TABLE PUBLIC.CHARGE_SESSION(
     id BIGINT PRIMARY KEY NOT NULL,
     session_id VARCHAR(255),
     start_time BIGINT,
     end_time BIGINT,
     total_cost NUMERIC(20, 2),
     DATE_CREATED BIGINT,
     DATE_UPDATED BIGINT,
     CHARGEPOINT_ID BIGINT,
     VEHICLE_ID BIGINT,
     CONSTRAINT charge_session_to_charge_point_id FOREIGN KEY (CHARGEPOINT_ID) REFERENCES CHARGE_POINT (ID),
     CONSTRAINT charge_session_to_vehicle_id FOREIGN KEY (VEHICLE_ID) REFERENCES VEHICLE (ID)
);