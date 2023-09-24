package co.bk.task.restapi.model;

import com.google.common.collect.Lists;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * ChargePoint has a 1:M relationship with ChargeSession
 *
 * "Charging Power" represents the rate at which the battery is being charged, measured in kilowatts (kW).
 */
@Entity
@Table(name = "charge_point")
public class ChargePoint {

    @Id
    @SequenceGenerator(name = "mySeqGen", sequenceName = "hibernate_sequence",  allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen")
    private Long id;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "chargePoint")
    private List<ChargeSession> detailRecords = Lists.newArrayList();

    @Column(name = "manufacturer_model")
    private String manufacturerModel;

    @Column(name = "charging_power_kw")
    private Double chargingPowerKw;

    @Column(name = "lat")
    private String latitude;

    @Column(name = "lon")
    private String longitude;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ChargePoint.Status status = Status.ACTIVE;

    // UTC time
    @Column(name = "date_created")
    private Long dateCreated = System.currentTimeMillis();

    // UTC time
    @Column(name = "date_updated")
    private Long dateUpdated;

    public ChargePoint() {}

    // Arg constructor provided to make object creation easier in integration tests (leverage ObjectMother pattern)
    public ChargePoint(String manufacturerModel, Double chargingPowerKw, String latitude, String longitude, ChargePoint.Status status) {
        this.manufacturerModel = manufacturerModel;
        this.chargingPowerKw = chargingPowerKw;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = Status.ACTIVE;
    }

    public static ChargePoint createChargePoint(String manufacturerModel, Double chargingPowerKw) {
        return new ChargePoint(manufacturerModel, chargingPowerKw, "17.23456", "32.67890", Status.ACTIVE);
    }

    /**
     * ACTIVE -> charge point is active on the charging network. Vehicles can find it and charge at it.
     * OUT_OF_SERVICE -> charge point is not active on the charging network.
     */
    public static enum Status {
        ACTIVE,
        OUT_OF_SERVICE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ChargeSession> getDetailRecords() {
        return detailRecords;
    }

    public void setDetailRecords(List<ChargeSession> detailRecords) {
        this.detailRecords = detailRecords;
    }

    public String getManufacturerModel() {
        return manufacturerModel;
    }

    public void setManufacturerModel(String manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Double getChargingPowerKw() {
        return chargingPowerKw;
    }

    public void setChargingPowerKw(Double chargingPowerKw) {
        this.chargingPowerKw = chargingPowerKw;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (getClass() != o.getClass()) {
            return false;
        }

        ChargePoint other = (ChargePoint) o;
        if (id != null ? !id.equals(other.id) : other.id != null) return false;
        if (manufacturerModel != null ? !manufacturerModel.equals(other.manufacturerModel) : other.manufacturerModel != null) return false;
        if (dateCreated != null ? !dateCreated.equals(other.dateCreated) : other.dateCreated != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (manufacturerModel != null ? manufacturerModel.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? Long.valueOf(dateCreated).hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
