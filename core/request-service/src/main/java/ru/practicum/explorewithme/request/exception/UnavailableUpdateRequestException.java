package ru.practicum.explorewithme.request.exception;

import lombok.Getter;

@Getter
public class UnavailableUpdateRequestException extends RuntimeException {
    private final long eventId;

    public UnavailableUpdateRequestException(long eventId) {
        super(String.format("Update to request with event id '%d' is unavailable", eventId));
        this.eventId = eventId;
    }
}
