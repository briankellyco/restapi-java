package co.bk.task.restapi.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/*
 * Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeSessionDto {

    private Long id;
    private String sessionId;
    private Long startTime;
    private Long endTime;
    private BigDecimal totalCost;
    private Long vehicleId;
    private Long chargePointId;
}
