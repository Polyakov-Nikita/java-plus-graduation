package ru.practicum.explorewithme.event.exception;

import lombok.Getter;

@Getter
public class EventNotFoundException extends RuntimeException {
    private final long id;

    public EventNotFoundException(long id) {
        super("Event with id '%d' not exists".formatted(id));
        this.id = id;
    }
}
