package co.bk.task.restapi.web.api;

import co.bk.task.restapi.service.dto.ChargeSessionDto;
import co.bk.task.restapi.web.api.cmd.ChargeSessionSaveCmd;
import co.bk.task.restapi.web.exceptionhandling.ApplicationException;
import co.bk.task.restapi.util.SortParameterEnum;
import co.bk.task.restapi.service.ChargeSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class ChargeSessionController {

    @Autowired
    ChargeSessionService chargeSessionService;

    /**
     * Get all charge detail records for a given vehicle and support sorting
     *   curl -X GET --header "Content-type: application/json" --header "Accept: application/json" http://localhost:8080/charge-sessions?vehicleId=10&sort=endTime
     *
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/charge-sessions", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getAllForVehicle(@RequestParam(required = true) Long vehicleId, @RequestParam(required = false) String sort) {

        SortParameterEnum sortedBy = validateSortParameter(sort);

        List<ChargeSessionDto> detailRecordList = chargeSessionService.getChargeSessionsForVehicleSorted(vehicleId, sortedBy);
        return new ResponseEntity<Object>(detailRecordList, HttpStatus.OK);
    }

    /**
     * Get specific charge-session:
     *   curl -X GET --header "Content-type: application/json" --header "Accept: application/json" http://localhost:8080/charge-sessions/20
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/charge-sessions/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getChargeDetailRecord(HttpServletResponse response, @PathVariable("id") final Long id) {

        ChargeSessionDto detailRecord = chargeSessionService.getChargeSessionById(id);
        return new ResponseEntity<Object>(detailRecord, HttpStatus.OK);
    }


    /**
     * Create a charge session
     *   curl -X POST --header "Content-type: application/json" --header "Accept: application/json"  --data '{"vehicleId":10, "chargePointId":1}' http://localhost:8080/charge-sessions
     *
     * @param chargeSessionSaveCmd
     * @param request
     * @param response
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/charge-sessions", method = RequestMethod.POST)
    public void save(@RequestBody ChargeSessionSaveCmd chargeSessionSaveCmd,
                     HttpServletRequest request,
                     HttpServletResponse response
    )  {

        chargeSessionSaveCmd.isDataValid();

        ChargeSessionDto chargeSessionDto = chargeSessionService.createChargeSession(chargeSessionSaveCmd.getVehicleId(), chargeSessionSaveCmd.getChargePointId());

        response.setHeader("Location", new StringBuffer(request.getContextPath())
                .append("/charge-sessions/").append(chargeSessionDto.getId()).toString());
    }


    /**
     * End a charging session and in so doing update the charge detail record (and calculate session cost)
     *   curl -X PUT --header "Content-type: application/json" --header "Accept: application/json"  --data '{}' http://localhost:8080/charge-sessions/23
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/charge-sessions/{id}", method = RequestMethod.PUT)
    public void update(HttpServletResponse response, @PathVariable("id") final Long id) {

        chargeSessionService.updateChargeSession(id);
    }

    /*
     * Allowed params "startTime, -startTime, endTime, -endTime". Any other param return a 400 bad request according to JSON spec.
     *
     * API standard for sort parameters:
     *   https://jsonapi.org/format/#fetching-sorting
     */
    private SortParameterEnum validateSortParameter(String sortParamToIdentify) {

        if (sortParamToIdentify == null || sortParamToIdentify.isEmpty()) {
            return SortParameterEnum.START_TIME_ASC;
        }

        Optional<SortParameterEnum> result = SortParameterEnum.identifySortParameter(sortParamToIdentify);

        if (!result.isPresent()) throw new ApplicationException(ApplicationException.ErrorCode.INVALID_SORT_PARAMETER);

        return result.get();
    }


}
