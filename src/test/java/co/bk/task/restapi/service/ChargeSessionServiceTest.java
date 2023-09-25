package co.bk.task.restapi.service;

import co.bk.task.restapi.model.ChargePoint;
import co.bk.task.restapi.model.ChargeSession;
import co.bk.task.restapi.model.Vehicle;
import co.bk.task.restapi.repository.ChargePointRepository;
import co.bk.task.restapi.repository.ChargeSessionListRepository;
import co.bk.task.restapi.repository.VehicleRepository;
import co.bk.task.restapi.service.converter.ChargeSessionConverter;
import co.bk.task.restapi.service.dto.ChargeSessionDto;
import co.bk.task.restapi.util.SortParameterEnum;
import co.bk.task.restapi.web.exceptionhandling.ApplicationException;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChargeSessionServiceTest {

    private static Long VEHICLE_ID = 2001L;
    private static Long CHARGE_SESSION_ONE = 3001L;
    private static Long CHARGE_SESSION_TWO = 3002L;

    private static Long CHARGE_POINT_ID = 4001L;


    ChargeSessionService serviceUnderTest;

    @Mock
    ChargeSessionListRepository chargeSessionListRepository;

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    ChargePointRepository chargePointRepository;

    @Mock
    ChargeSessionConverter chargeSessionConverter;

    @BeforeEach
    void setup() {
        // Manual instantiation instead of using @InjectMocks as a value needs to be assigned to the @Value annotated field (via constructor using Lombok)
        serviceUnderTest = new ChargeSessionService(Double.valueOf("0.50"), chargeSessionListRepository, vehicleRepository, chargePointRepository, chargeSessionConverter);
    }

    @Test
    void testGetChargeSessionsForVehicleSorted() {

        // given
        ChargeSession chargeSession = spy(ChargeSession.class);
        chargeSession.setId(CHARGE_SESSION_ONE);
        ChargeSessionDto chargeSessionDto = spy(ChargeSessionDto.class);
        chargeSessionDto.setId(CHARGE_SESSION_ONE);
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicleRepository.findById(any())).thenReturn(Optional.of(vehicle));

        when(chargeSessionListRepository.findChargeSessionsByVehicleId(any())).thenReturn(List.of(chargeSession));
        when(chargeSessionConverter.convert(chargeSession)).thenReturn(chargeSessionDto);

        // when
        List<ChargeSessionDto> chargeSessionDtoList = serviceUnderTest.getChargeSessionsForVehicleSorted(VEHICLE_ID, SortParameterEnum.START_TIME_ASC);

        // then
        assertEquals(1, chargeSessionDtoList.size());

    }

    @Test
    void testGetChargeSessionsForVehicleSorted_no_charge_sessions_exist() {

        // given
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicleRepository.findById(any())).thenReturn(Optional.of(vehicle));
        when(chargeSessionListRepository.findChargeSessionsByVehicleId(any())).thenReturn(Lists.newArrayList());

        // when
        List<ChargeSessionDto> chargeSessionDtoList = serviceUnderTest.getChargeSessionsForVehicleSorted(VEHICLE_ID, SortParameterEnum.START_TIME_ASC);

        // then
        assertEquals(0, chargeSessionDtoList.size());

    }

    @Test
    void testGetChargeSessionsForVehicleSorted_error_RECORD_NOT_FOUND_FOR_VEHICLE() {

        // given
        ChargeSession chargeSession = spy(ChargeSession.class);
        chargeSession.setId(CHARGE_SESSION_ONE);
        ChargeSessionDto chargeSessionDto = spy(ChargeSessionDto.class);
        chargeSessionDto.setId(CHARGE_SESSION_ONE);

        when(vehicleRepository.findById(any())).thenThrow(new ApplicationException(
                ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_VEHICLE, new String[] { String.valueOf(VEHICLE_ID) }));

        // when & then
        assertThatThrownBy(() -> serviceUnderTest.getChargeSessionsForVehicleSorted(10L, SortParameterEnum.START_TIME_ASC))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(String.format(
                        ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_VEHICLE.getMessage(), VEHICLE_ID));

    }

    @Test
    void testGetChargeSessionById() {

        // given
        ChargeSession chargeSession = spy(ChargeSession.class);
        chargeSession.setId(CHARGE_SESSION_ONE);
        ChargeSessionDto chargeSessionDto = spy(ChargeSessionDto.class);
        chargeSessionDto.setId(CHARGE_SESSION_ONE);
        Vehicle vehicle = mock(Vehicle.class);

        when(chargeSessionListRepository.findById(any())).thenReturn(Optional.of(chargeSession));
        when(chargeSessionConverter.convert(chargeSession)).thenReturn(chargeSessionDto);

        // when
        chargeSessionDto = serviceUnderTest.getChargeSessionById(CHARGE_SESSION_ONE);

        // then
        assertEquals(CHARGE_SESSION_ONE, chargeSessionDto.getId());
    }

    @Test
    void testGetChargeSessionById_error_RECORD_NOT_FOUND_FOR_CHARGE_SESSION() {

        // given
        ChargeSession chargeSession = spy(ChargeSession.class);
        chargeSession.setId(CHARGE_SESSION_ONE);

        when(chargeSessionListRepository.findById(any())).thenThrow(
                new ApplicationException(ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_CHARGE_SESSION,
                    new String[] { String.valueOf(CHARGE_SESSION_ONE) }));

        // when & then
        assertThatThrownBy(() -> serviceUnderTest.getChargeSessionById(CHARGE_SESSION_ONE))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(String.format(
                        ApplicationException.ErrorCode.RECORD_NOT_FOUND_FOR_CHARGE_SESSION.getMessage(), CHARGE_SESSION_ONE));
    }


    @Test
    void testCreateChargeSession() {

        // given
        ChargePoint chargePoint = spy(ChargePoint.class);
        chargePoint.setId(CHARGE_POINT_ID);
        chargePoint.setChargingPowerKw(50.0);

        ChargeSession chargeSessionNoEndTime = spy(ChargeSession.class);
        chargeSessionNoEndTime.setId(CHARGE_SESSION_TWO);
        chargeSessionNoEndTime.setChargePoint(chargePoint);

        Vehicle vehicle = spy(Vehicle.class);
        vehicle.setId(VEHICLE_ID);
        vehicle.setBatteryCapacityKwh(94.5);
        vehicle.setBatteryLevelPercent(20.0);
        chargeSessionNoEndTime.setVehicle(vehicle);
        vehicle.setChargeSessions(List.of(chargeSessionNoEndTime));
        when(vehicleRepository.findById(any())).thenReturn(Optional.of(vehicle));
        when(chargePointRepository.findById(any())).thenReturn(Optional.of(chargePoint));

        ChargeSession chargeSessionNow = spy(ChargeSession.class);
        chargeSessionNow.setId(CHARGE_SESSION_ONE);
        when(chargeSessionListRepository.save(any())).thenReturn(chargeSessionNow);

        ChargeSessionDto chargeSessionDto = spy(ChargeSessionDto.class);
        chargeSessionDto.setId(CHARGE_SESSION_ONE);
        when(chargeSessionConverter.convert(chargeSessionNow)).thenReturn(chargeSessionDto);

        // when
        chargeSessionDto = serviceUnderTest.createChargeSession(VEHICLE_ID, CHARGE_POINT_ID);

        // then
        assertEquals(CHARGE_SESSION_ONE, chargeSessionDto.getId());
    }

    @Test
    void updateChargeSession() {

        ChargePoint chargePoint = spy(ChargePoint.class);
        chargePoint.setId(CHARGE_POINT_ID);
        chargePoint.setChargingPowerKw(50.0);

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(CHARGE_SESSION_ONE);
        chargeSession.setChargePoint(chargePoint);
        chargeSession.setStartTime(Instant.now().minus(4, ChronoUnit.SECONDS).toEpochMilli());

        Vehicle vehicle = spy(Vehicle.class);
        vehicle.setId(VEHICLE_ID);
        vehicle.setBatteryCapacityKwh(94.5);
        vehicle.setBatteryLevelPercent(20.0);
        chargeSession.setVehicle(vehicle);

        when(chargeSessionListRepository.findById(any())).thenReturn(Optional.of(chargeSession));

        // when
        serviceUnderTest.updateChargeSession(CHARGE_SESSION_ONE);

        // then
        verify(chargeSessionListRepository, times(1)).save(any());
    }


}
