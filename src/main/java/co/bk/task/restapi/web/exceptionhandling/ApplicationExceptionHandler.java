package co.bk.task.restapi.web.exceptionhandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zalando.problem.*;
import org.zalando.problem.Status;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 *
 *  {
 *      title: "Not found",
 *      status: 404,
 *      detail: "The message from the specific exception detailing what the problem was.",
 *      application_code: "custom code that uniquely identifies the exception case"
 *  }
 *
 */
@ControllerAdvice
public class ApplicationExceptionHandler {

    static final Map<String, Status> VALID_HTTP_STATUS_CODES =
            Arrays.stream(Status.values()).collect(Collectors.toMap(item -> String.valueOf(item.getStatusCode()), Function.identity() ));

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Problem> handleRuntimeException(RuntimeException ex) {

        // Defaults
        Status status = Status.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();
        String applicationCode = "APPLICATION-CODE-NOT-DEFINED";

        if (ex instanceof ApplicationException) {

            ApplicationException aex = (ApplicationException) ex;

            if (aex.getMessage() != null && aex.getMessage().length() >= 3) {

                final String codeFromMessage = aex.getMessage().substring(0,3);
                Status statusFromMessage = VALID_HTTP_STATUS_CODES.get(codeFromMessage);
                if (statusFromMessage != null) {
                    status = statusFromMessage;
                    message = aex.getMessage().substring(4);
                    applicationCode = aex.getApplicationCode();
                }
            }
        }

        return ResponseEntity
                .status(HttpStatus.valueOf(status.getStatusCode()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString())
                .body(
                        Problem.builder()
                                .withTitle(status.getReasonPhrase())
                                .withStatus(status)
                                .withDetail(message)
                                .with("application_code", applicationCode)
                                .build()
                );
    }

}
