package ru.practicum.explorewithme.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ErrorResponseBuilder {
    private final String senderName;

    public ErrorResponseBuilder(String senderName) {
        this.senderName = senderName;
    }

    public ResponseEntity<ApiError> createErrorResponse(String message, String reason, HttpStatus status) {
        return ResponseEntity.status(status).body(
                new ApiError(
                        String.format("[%s]: %s", senderName, message),
                        reason,
                        status.toString(),
                        LocalDateTime.now()
                )
        );
    }
}
