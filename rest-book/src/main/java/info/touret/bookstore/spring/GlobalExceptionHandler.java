package info.touret.bookstore.spring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import info.touret.bookstore.spring.book.exception.ApiCallTimeoutException;
import info.touret.bookstore.spring.maintenance.exception.MaintenanceException;

/**
 * Handles all the exceptions thrown by the application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Indicates that the application is on maintenance
     */
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler(MaintenanceException.class)
    public APIError maintenance() {
        return new APIError(HttpStatus.I_AM_A_TEAPOT.value(),"Service currently in maintenance");
    }

    /**
     * Indicates there is a timeout
     */
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler({ApiCallTimeoutException.class})
    public APIError timeoutException() {
        return new APIError(HttpStatus.REQUEST_TIMEOUT.value(),"A timeout occured");
    }


    /**
     * Any other exception
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public APIError anyException() {
        return new APIError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"An unexpected server error occured");
    }
}
