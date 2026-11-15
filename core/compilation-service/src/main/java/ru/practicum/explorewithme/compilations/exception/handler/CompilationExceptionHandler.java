package ru.practicum.explorewithme.compilations.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;
import ru.practicum.explorewithme.compilations.exception.CompilationNotFoundException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class CompilationExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> userNotFound(CompilationNotFoundException e) {
        return responseBuilder.createErrorResponse("compilation was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
