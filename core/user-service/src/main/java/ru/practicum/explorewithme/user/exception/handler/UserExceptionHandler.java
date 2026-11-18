package ru.practicum.explorewithme.user.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;
import ru.practicum.explorewithme.user.exception.DuplicatedEmailException;
import ru.practicum.explorewithme.user.exception.UserNotFoundException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class UserExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> duplicatedEmail(DuplicatedEmailException e) {
        return responseBuilder.createErrorResponse("duplicated email", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> userNotFound(UserNotFoundException e) {
        return responseBuilder.createErrorResponse("user was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
