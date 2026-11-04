package ru.practicum.explorewithme.category.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.category.exception.CategoryNotFoundException;
import ru.practicum.explorewithme.category.exception.DuplicatedNameException;
import ru.practicum.explorewithme.category.exception.NotEmptyCategoryException;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;

@RestControllerAdvice
@SuppressWarnings("unused")
public class CategoryExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> duplicatedEmail(DuplicatedNameException e) {
        return responseBuilder.createErrorResponse("duplicated name", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> categoryNotFound(CategoryNotFoundException e) {
        return responseBuilder.createErrorResponse("category was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> notEmptyCategory(NotEmptyCategoryException e) {
        return responseBuilder.createErrorResponse("trying delete category with events", e.getMessage(), HttpStatus.CONFLICT);
    }
}
