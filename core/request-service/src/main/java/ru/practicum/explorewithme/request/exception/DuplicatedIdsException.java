package ru.practicum.explorewithme.request.exception;

import lombok.Getter;

@Getter
public class DuplicatedIdsException extends RuntimeException {
    private final long eventId;
    private final long userId;

    public DuplicatedIdsException(long eventId, long userId) {
        super(String.format("Request with event id '%d' and user id '%d' already exists", eventId, userId));
        this.eventId = eventId;
        this.userId = userId;
    }
}
