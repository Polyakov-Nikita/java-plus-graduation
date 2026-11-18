package ru.practicum.explorewithme.event.exception;

import lombok.Getter;

@Getter
public class UnavailableUpdateEventException extends RuntimeException {
    private final long id;

    public UnavailableUpdateEventException(long id) {
        super("Update to event with id '%d' is unavailable".formatted(id));
        this.id = id;
    }
}
