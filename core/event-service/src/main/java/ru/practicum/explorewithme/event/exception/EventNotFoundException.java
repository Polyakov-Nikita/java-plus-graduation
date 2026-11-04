package ru.practicum.explorewithme.event.exception;

import lombok.Getter;

@Getter
public class EventNotFoundException extends RuntimeException {
    private final long id;

    public EventNotFoundException(long id) {
        super(String.format("Event with id '%d' not exists", id));
        this.id = id;
    }
}
