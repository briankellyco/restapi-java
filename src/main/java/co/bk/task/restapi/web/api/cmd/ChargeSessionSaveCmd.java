package co.bk.task.restapi.web.api.cmd;

import co.bk.task.restapi.web.exceptionhandling.ApplicationException;

/**
 * Command pattern wrapping JSON data submitted to REST endpoint.
 */
public class ChargeSessionSaveCmd {

    private Long vehicleId;

    private Long chargePointId;

    /**
     * Check integrity of data submitted.
     */
    public void isDataValid() {

        if (vehicleId == null || chargePointId == null) {
            throw new ApplicationException(ApplicationException.ErrorCode.SAVE_SESSION_INCOMPLETE);
        }
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getChargePointId() {
        return chargePointId;
    }

    public void setChargePointId(Long chargePointId) {
        this.chargePointId = chargePointId;
    }
}