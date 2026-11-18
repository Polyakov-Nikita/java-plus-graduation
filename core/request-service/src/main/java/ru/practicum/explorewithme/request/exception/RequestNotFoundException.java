package ru.practicum.explorewithme.request.exception;

import lombok.Getter;

@Getter
public class RequestNotFoundException extends RuntimeException {
    private final long id;

    public RequestNotFoundException(long id) {
        super("Request with id '%d' not exists".formatted(id));
        this.id = id;
    }
}
