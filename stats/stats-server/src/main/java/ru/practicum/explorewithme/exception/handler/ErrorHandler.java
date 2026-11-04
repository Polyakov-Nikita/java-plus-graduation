package ru.practicum.explorewithme.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;
import ru.practicum.explorewithme.exception.WrongDateIntervalException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class ErrorHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> unexpected(Throwable e) {
        return responseBuilder.createErrorResponse(
                "SERVICE: unexpected error",
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> methodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        if (fieldError == null) {
            return unexpected(e);
        }
        return responseBuilder.createErrorResponse(
                "incorrect field value",
                String.format(
                        "value of field '%s'=%s is incorrect, cause: %s",
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> wrongDateInterval(WrongDateIntervalException e) {
        return responseBuilder.createErrorResponse(
                "wrong date interval",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> missingServletRequestParameter(MissingServletRequestParameterException e) {
        return responseBuilder.createErrorResponse(
                "missing request parameter",
                e.getBody().getDetail(),
                HttpStatus.BAD_REQUEST
        );
    }
}
