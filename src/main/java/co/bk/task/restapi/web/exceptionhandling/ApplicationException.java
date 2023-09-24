package co.bk.task.restapi.web.exceptionhandling;

/**
 * Service exception.
 */
public class ApplicationException extends RuntimeException {

    private transient ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public ApplicationException(ErrorCode errorCode, String[] paramsForMessage) {
        this(errorCode, String.format(errorCode.getMessage(), paramsForMessage));
    }

    /**
     * Construct exception with underlining exception.
     *
     * @param errorCode errorCode.
     * @param cause     - {@link Throwable} underlining exception.
     */
    public ApplicationException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor.
     *
     * @param errorCode error code.
     * @param message   message.
     */
    public ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.toString();
    }

    /**
     * Return the ErrorCode.
     * @return - ErrorCode
     */
    public ErrorCode getCode() {
        return errorCode;
    }

    /**
     * Return the unique application error code.
     *
     * @return an error code or &quot;notdefined&quot;
     */
    public String getApplicationCode() {
        String applicationCode = "UNDEFINED";
        if (errorCode != null) {
            applicationCode = errorCode.getApplicationCode();
        }
        return applicationCode;
    }

    /**
     * Error Code enum.
     */
    public enum ErrorCode {

        SAVE_SESSION_INCOMPLETE("RESTAPI-0001", "400 Charge Session could not be saved due to incomplete data."),
        RECORD_NOT_FOUND_FOR_VEHICLE("RESTAPI-0002", "404 Vehicle Record with ID %s not found."),

        RECORD_NOT_FOUND_FOR_CHARGE_POINT("RESTAPI-0003", "404 Charge Point with ID %s not found."),

        //UPDATE_DETAILS_INCOMPLETE("RESTAPI-0007", "Record could not be updated due to incomplete data."),

        INVALID_SORT_PARAMETER("RESTAPI-0008", "400 Invalid sort parameter supplied. Only startTime, -startTime, endTime and -endTime supported."),

        RECORD_NOT_FOUND_FOR_CHARGE_SESSION("RESTAPI-0010", "404 Charge Session with id %s not found.");

        private String applicationCode;
        private String message = "No description provided";

        private ErrorCode() {
        }

        private ErrorCode(String applicationCode, String message) {
            this.applicationCode = applicationCode;
            this.message = message;
        }

        public String getApplicationCode() {
            return applicationCode;
        }

        public String getMessage() {
            return message;
        }

    }
}

