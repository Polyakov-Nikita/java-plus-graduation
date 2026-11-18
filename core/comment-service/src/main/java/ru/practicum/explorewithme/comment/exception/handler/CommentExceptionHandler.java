package ru.practicum.explorewithme.comment.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.comment.exception.CommentNotFoundException;
import ru.practicum.explorewithme.comment.exception.UnavailableUpdateCommentException;
import ru.practicum.explorewithme.comment.exception.UserNotFoundException;
import ru.practicum.explorewithme.common.exception.handler.ApiError;
import ru.practicum.explorewithme.common.exception.handler.ErrorResponseBuilder;

@RestControllerAdvice
@SuppressWarnings("unused")
public class CommentExceptionHandler {
    private final ErrorResponseBuilder responseBuilder = new ErrorResponseBuilder(getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ApiError> commentNotFound(CommentNotFoundException e) {
        return responseBuilder.createErrorResponse("comment was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> userNotFound(UserNotFoundException e) {
        return responseBuilder.createErrorResponse("user was not found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> unavailableUpdateComment(UnavailableUpdateCommentException e) {
        return responseBuilder.createErrorResponse(e.getMessage(), "comment update is unavailable", HttpStatus.CONFLICT);
    }
}
