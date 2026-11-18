package ru.practicum.explorewithme.request.exception;

import lombok.Getter;

@Getter
public class UnavailableUpdateRequestException extends RuntimeException {
    private final long eventId;

    public UnavailableUpdateRequestException(long eventId) {
        super("Update to request with event id '%d' is unavailable".formatted(eventId));
        this.eventId = eventId;
    }
}
