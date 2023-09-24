package co.bk.task.restapi.model;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DetailRecord has a M:1 relationship with Vehicle.
 *
 * Stores the details of a charging session at a charging point for a given vehicle.
 */
@Entity
@Table(name = "charge_session")
public class ChargeSession {

    @Id
    @SequenceGenerator(name = "mySeqGen", sequenceName = "hibernate_sequence",  allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private ChargePoint chargePoint;

    @Column(name = "session_id")
    private String sessionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Vehicle vehicle;

    @Column(name = "start_time")
    private Long startTime = System.currentTimeMillis();

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "total_cost", precision=18, scale=8)
    private BigDecimal totalCost;

    @Column(name = "date_created")
    private Long dateCreated = System.currentTimeMillis();

    @Column(name = "date_updated")
    private Long dateUpdated;

    public ChargeSession() {}

    public ChargeSession(Vehicle vehicle, ChargePoint chargePoint) {
        this.vehicle = vehicle;
        this.chargePoint = chargePoint;
        this.sessionId = generateSessionId();
    }

    public ChargeSession(Vehicle vehicle, ChargePoint chargePoint, BigDecimal totalCost, Long endTime) {
        this.vehicle = vehicle;
        this.chargePoint = chargePoint;
        this.sessionId = generateSessionId();
        this.totalCost = totalCost;
        this.endTime = endTime;
    }

    public static ChargeSession createChargeSession(Vehicle vehicle, ChargePoint chargePoint, BigDecimal totalCost, Long endTime) {
        return new ChargeSession(vehicle, chargePoint, totalCost, endTime);
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChargePoint getChargePoint() {
        return chargePoint;
    }

    public void setChargePoint(ChargePoint chargePoint) {
        this.chargePoint = chargePoint;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (getClass() != o.getClass()) {
            return false;
        }

        ChargeSession other = (ChargeSession) o;
        if (id != null ? !id.equals(other.id) : other.id != null) return false;
        if (sessionId != null ? !sessionId.equals(other.sessionId) : other.sessionId != null) return false;
        if (dateCreated != null ? !dateCreated.equals(other.dateCreated) : other.dateCreated != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? Long.valueOf(dateCreated).hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
