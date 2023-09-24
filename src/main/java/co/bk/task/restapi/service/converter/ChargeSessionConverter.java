package co.bk.task.restapi.service.converter;

import co.bk.task.restapi.model.ChargeSession;
import co.bk.task.restapi.service.dto.ChargeSessionDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChargeSessionConverter implements Converter<ChargeSession, ChargeSessionDto> {

    @Override
    public ChargeSessionDto convert(final ChargeSession input) {
        final ChargeSessionDto chargeSessionDto = new ChargeSessionDto();
        chargeSessionDto.setId(input.getId());
        chargeSessionDto.setSessionId(input.getSessionId());
        chargeSessionDto.setStartTime(input.getStartTime());
        chargeSessionDto.setEndTime(input.getEndTime());
        chargeSessionDto.setTotalCost(input.getTotalCost());
        chargeSessionDto.setVehicleId((input.getVehicle() != null) ? input.getVehicle().getId() : null);
        chargeSessionDto.setChargePointId((input.getChargePoint() != null) ? input.getChargePoint().getId() : null);
        return chargeSessionDto;
    }
}
