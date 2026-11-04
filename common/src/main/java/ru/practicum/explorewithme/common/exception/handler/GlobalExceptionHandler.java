package ru.practicum.explorewithme.common.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> unexpected(RuntimeException e) {
        return responseBuilder.createErrorResponse(
                e.getMessage(),
                "unexpected error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> methodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        if (fieldError == null) {
            return responseBuilder.createErrorResponse("", "incorrect field value", HttpStatus.BAD_REQUEST);
        }
        return responseBuilder.createErrorResponse(
                String.format(
                        "value of field '%s'=%s is incorrect, cause: %s",
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()),
                "incorrect field value",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> constraintViolation(ConstraintViolationException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "incorrect field value", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "incorrect field value", HttpStatus.BAD_REQUEST);
    }
}
