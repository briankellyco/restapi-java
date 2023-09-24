package co.bk.task.restapi.model;

import com.google.common.collect.Lists;
import jakarta.persistence.*;

import java.util.List;

/**
 * Vehicle has a 1:M relationship with ChargeSession
 *
 * Battery Level Percent represents the State of Charge and describes how full your battery is, in terms of percentage. Think of it as a fuel gauge.
 * It represents the maximum amount of energy the electric vehicle's battery can store, measured in kilowatt-hours (kWh).
 */
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @SequenceGenerator(name = "mySeqGen", sequenceName = "hibernate_sequence",  allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen")
    private Long id;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "vehicle")
    private List<ChargeSession> chargeSessions = Lists.newArrayList();

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Vehicle.Status status = Status.ACTIVE;

    @Column(name = "battery_capacity_kwh")
    private Double batteryCapacityKwh;

    @Column(name = "battery_level_percent")
    private Double batteryLevelPercent;

    // UTC time
    @Column(name = "date_created")
    private Long dateCreated = System.currentTimeMillis();

    // UTC time
    @Column(name = "date_updated")
    private Long dateUpdated;

    // No-arg constructor always required for entities
    public Vehicle() {}

    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    // Arg constructor provided to make object creation easier in integration tests
    public Vehicle(String licensePlate, Vehicle.Status status, Double batteryCapacityKwh, Double batteryLevelPercent) {
        this.licensePlate = licensePlate;
        this.status = status;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.batteryLevelPercent = batteryLevelPercent;
    }

    public static Vehicle createVehicle(String licensePlate) {
        return new Vehicle(licensePlate, Status.ACTIVE, 120.0, 20.0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public List<ChargeSession> getChargeSessions() {
        return chargeSessions;
    }

    public void setChargeSessions(List<ChargeSession> chargeSessions) {
        this.chargeSessions = chargeSessions;
    }

    public Double getBatteryCapacityKwh() {
        return batteryCapacityKwh;
    }

    public void setBatteryCapacityKwh(Double batteryCapacityKwh) {
        this.batteryCapacityKwh = batteryCapacityKwh;
    }

    public Double getBatteryLevelPercent() {
        return batteryLevelPercent;
    }

    public void setBatteryLevelPercent(Double batteryLevelPercent) {
        this.batteryLevelPercent = batteryLevelPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (getClass() != o.getClass()) {
            return false;
        }

        Vehicle other = (Vehicle) o;
        if (id != null ? !id.equals(other.id) : other.id != null) return false;
        if (licensePlate != null ? !licensePlate.equals(other.licensePlate) : other.licensePlate != null) return false;
        if (dateCreated != null ? !dateCreated.equals(other.dateCreated) : other.dateCreated != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? Long.valueOf(dateCreated).hashCode() : 0);
        return result;
    }

    /**
     * ACTIVE -> vehicle is active on the system for the user account
     * NOT_ACTIVE -> indicates vehicle is no longer active. User account has activated a different vehicle.
     */
    public static enum Status {
        ACTIVE,
        NOT_ACTIVE;
    }

}
