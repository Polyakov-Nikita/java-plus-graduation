package ru.practicum.explorewithme.event.exception;

import lombok.Getter;

@Getter
public class UserNotAttendEventException extends RuntimeException {
    private final long userId;
    private final long eventId;

    public UserNotAttendEventException(long userId, long eventId) {
        super("User with id '%d' did not attend event with id '%d'".formatted(userId, eventId));
        this.userId = userId;
        this.eventId = eventId;
    }
}
