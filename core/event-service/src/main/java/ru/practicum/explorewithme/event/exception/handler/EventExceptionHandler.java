package ru.practicum.explorewithme.event.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;
import ru.practicum.explorewithme.event.exception.EarlyDateException;
import ru.practicum.explorewithme.event.exception.EventNotFoundException;
import ru.practicum.explorewithme.event.exception.UnavailableUpdateEventException;
import ru.practicum.explorewithme.event.exception.WrongDateIntervalException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class EventExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> userNotFound(EventNotFoundException e) {
        return responseBuilder.createErrorResponse("event was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> unavailableUpdateEvent(UnavailableUpdateEventException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "event update is unavailable", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> earlyDate(EarlyDateException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "date is early", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> wrongInterval(WrongDateIntervalException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "wrong date interval", HttpStatus.BAD_REQUEST);
    }
}
