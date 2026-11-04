package ru.practicum.explorewithme.request.exception;

import lombok.Getter;

@Getter
public class RequestNotFoundException extends RuntimeException {
    private final long id;

    public RequestNotFoundException(long id) {
        super(String.format("Request with id '%d' not exists", id));
        this.id = id;
    }
}
