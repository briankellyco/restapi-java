package co.bk.task.restapi.service;

import co.bk.task.restapi.service.converter.ChargeSessionConverter;
import co.bk.task.restapi.model.ChargePoint;
import co.bk.task.restapi.model.ChargeSession;
import co.bk.task.restapi.model.Vehicle;
import co.bk.task.restapi.repository.ChargePointRepository;
import co.bk.task.restapi.repository.VehicleRepository;
import co.bk.task.restapi.service.dto.ChargeSessionDto;
import co.bk.task.restapi.util.EndTimeComparator;
import co.bk.task.restapi.util.SortParameterEnum;
import co.bk.task.restapi.repository.ChargeSessionListRepository;
import co.bk.task.restapi.web.exceptionhandling.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeSessionService {

    public static final BigDecimal MINIMUM_CONNECTION_FEE = new BigDecimal(1);

    public static final Long MILLISECONDS_IN_HOUR = 60 * 60 * 1000L;

    @Value("${application.costOfPowerPerKwh}")
    private Double costOfPowerPerKwh = 1.0;

    //@Autowired
    private final ChargeSessionListRepository chargeSessionListRepository;

    //@Autowired
    private final VehicleRepository vehicleRepository;

    // @Autowired
    private final ChargePointRepository chargePointRepository;

    //@Autowired
    private final ChargeSessionConverter chargeSessionConverter;

    public List<ChargeSessionDto> getChargeSessionsForVehicleSorted(Long vehicleId, SortParameterEnum sortedBy) {

        List<ChargeSessionDto> chargeSessionVOList = getChargeSessionsForVehicle(vehicleId);

        switch (sortedBy) {
            case START_TIME_ASC:
                chargeSessionVOList.sort(Comparator.comparing(ChargeSessionDto::getStartTime));
                break;
            case START_TIME_DESC:
                chargeSessionVOList.sort(Comparator.comparing(ChargeSessionDto::getStartTime, Comparator.reverseOrder()));
                break;
            case END_TIME_ASC:
                Collections.sort(chargeSessionVOList, new EndTimeComparator());
                break;
            case END_TIME_DESC:
                Collections.sort(chargeSessionVOList, new EndTimeComparator().reversed());
                break;
        }
        return chargeSessionVOList;
    }

    /**
     * Get ChargeSessions for a specific vehicle
     * @param vehicleId unique id assigned to the vehicle (not its license plate number)
     * @return list of charge sessions for the vehicle
     */
    private List<ChargeSessionDto> getChargeSessionsForVehicle(Long vehicleId) {


        // Check vehicle exists
        vehicleRepository.findById(vehicleId).orElseThrow(() -> new ApplicationException(ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_VEHICLE, new String[] { String.valueOf(vehicleId) }));

        List<ChargeSession> chargeSessions = chargeSessionListRepository.findChargeSessionsByVehicleId(vehicleId);

        List<ChargeSessionDto> chargeSessionDtoList = chargeSessions.stream()
                .map(chargeSession -> {
                  return chargeSessionConverter.convert(chargeSession);
                } )
                .collect(Collectors.toList());

        return chargeSessionDtoList;
    }

    public ChargeSessionDto getChargeSessionById(long id) {

        ChargeSession chargeSession = chargeSessionListRepository.findById(id).orElseThrow(
                () -> new ApplicationException(ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_CHARGE_SESSION,
                        new String[] { String.valueOf(id) }));

        return chargeSessionConverter.convert(chargeSession);
    }

    @Transactional
    public ChargeSessionDto createChargeSession(Long vehicleId , Long chargePointId) {

        // Check vehicle exists
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new ApplicationException(ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_VEHICLE, new String[] { String.valueOf(vehicleId) }));

        /*
         * Business requirement:
         * - prevent creation of charge sessions where the new charge session has a startTime > endTime of previous records.
         *
         * Assumption:  customers tried to submit their end time when they last disconnected their charger but their app or this API failed
         * to process the end time (for some unknown reason). As a customer friendly business we do not want to overcharge them for this.
         */
        vehicle.getChargeSessions().stream()
                .filter(chargeSession -> chargeSession.getEndTime() == null)
                .findFirst()
                .ifPresent(chargeSession -> {
                    calculateCostAndEndSession(chargeSession, true);
                });

        // Check charge point exists
        ChargePoint chargePoint = chargePointRepository.findById(chargePointId).orElseThrow(() -> new ApplicationException(ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_CHARGE_POINT, new String[] { String.valueOf(chargePointId) }));

        ChargeSession chargeSessionNow = new ChargeSession(vehicle, chargePoint);
        ChargeSession chargeSessionSaved = chargeSessionListRepository.save(chargeSessionNow);
        return chargeSessionConverter.convert(chargeSessionSaved);
    }


    @Transactional
    public void updateChargeSession(Long id) {

        ChargeSession chargeSession = chargeSessionListRepository.findById(id).orElseThrow(
                () -> new ApplicationException(ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_CHARGE_SESSION,
                        new String[] { String.valueOf(id) }));

        calculateCostAndEndSession(chargeSession, false);

        chargeSessionListRepository.save(chargeSession);
    }

    /**
     * Calculate cost of charging session.
     *
     * Apply default cost to the charging session if the session was not ended (and a new session has just been
     * requested by the customer); this situation can occur if the customer was unable to end the session.
     *
     * Default cost is assigned so as not to overcharge a customer (we assume the customer did everything asked of them and the only
     * reason the session was not ended was due to a technical issue).
     *
     * @param chargeSession
     * @param assignDefaultCost true if the session does not possess an end time (and a new session has just been requested by the customer)
     */
    private void calculateCostAndEndSession(ChargeSession chargeSession, boolean assignDefaultCost) {

        // End charging session
        long utcTimestamp = System.currentTimeMillis();
        chargeSession.setEndTime(utcTimestamp);
        chargeSession.setDateUpdated(utcTimestamp);

        /*
         * Calculate cost of charging session where:
         *   Energy (kWh) = Power (kW) × Time (hours)
         * and
         *   Cost of Power (Euros) = Energy (kWh) × Cost of Power (Euros per kWh)
         */
        Double batteryCapacity = chargeSession.getVehicle().getBatteryCapacityKwh();
        Double batteryLevelPercentAtStart = chargeSession.getVehicle().getBatteryLevelPercent();
        Double chargingPowerKw = chargeSession.getChargePoint().getChargingPowerKw();

        if (assignDefaultCost) {
            // Default cost
            chargeSession.setTotalCost(MINIMUM_CONNECTION_FEE);

        } else {
            // Current charging session being ended. Calculate the cost of the session.

            // Energy (kWh) = Power (kW) × Time (hours)
            BigDecimal energyConsumedKwh = null;
            if (batteryLevelPercentAtStart < Double.valueOf("100")) {

                // Battery was not fully charged at the start of the session
                BigDecimal durationChargeSessionInHours = new BigDecimal(chargeSession.getEndTime() - chargeSession.getStartTime())
                        .divide(new BigDecimal(MILLISECONDS_IN_HOUR), new MathContext(18, RoundingMode.HALF_EVEN));

                BigDecimal theoreticalEnergyDeliveredDuringSession = new BigDecimal(chargingPowerKw).multiply(
                        durationChargeSessionInHours, new MathContext(18, RoundingMode.HALF_EVEN)); // KwH

                BigDecimal energyRequiredByBatteryToFullyCharge = new BigDecimal(String.valueOf(batteryCapacity * (100 - batteryLevelPercentAtStart) / 100)); // KwH

                if (energyRequiredByBatteryToFullyCharge.compareTo(theoreticalEnergyDeliveredDuringSession) > 0) {
                    // Battery was not fully charged at the end of the session
                    energyConsumedKwh = theoreticalEnergyDeliveredDuringSession;
                } else {
                    // Battery was fully charged at the end of the session
                    energyConsumedKwh = energyRequiredByBatteryToFullyCharge;
                }

                // Cost of Power (Euros) = Energy (kWh) × Cost of Power (Euros per kWh)
                BigDecimal costOfPower = energyConsumedKwh.multiply(
                        new BigDecimal(costOfPowerPerKwh), new MathContext(18, RoundingMode.HALF_EVEN));

                BigDecimal totalCost = costOfPower.add(MINIMUM_CONNECTION_FEE);
                chargeSession.setTotalCost(totalCost);

            } else {
                // Battery was full when the session started
                chargeSession.setTotalCost(MINIMUM_CONNECTION_FEE);
            }
        }
    }
}
