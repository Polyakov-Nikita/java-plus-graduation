package ru.practicum.explorewithme.request.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;
import ru.practicum.explorewithme.request.exception.ConfirmedRequestException;
import ru.practicum.explorewithme.request.exception.DuplicatedIdsException;
import ru.practicum.explorewithme.request.exception.RequestNotFoundException;
import ru.practicum.explorewithme.request.exception.UnavailableUpdateRequestException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class RequestExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> unavailableUpdateRequest(UnavailableUpdateRequestException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "request update is unavailable", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> duplicatedIds(DuplicatedIdsException e) {
        return responseBuilder.createErrorResponse("duplicated ids", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> requestNotFound(RequestNotFoundException e) {
        return responseBuilder.createErrorResponse("request was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> confirmedRequest(ConfirmedRequestException e) {
        return responseBuilder.createErrorResponse("request is confirmed", e.getMessage(), HttpStatus.CONFLICT);
    }
}
