package ru.practicum.explorewithme.request.exception;

import lombok.Getter;

@Getter
public class ConfirmedRequestException extends RuntimeException {
    private final long id;

    public ConfirmedRequestException(long id) {
        super(String.format("Trying cancel request with \"CONFIRMED\" status. Request id: '%d'", id));
        this.id = id;
    }
}
