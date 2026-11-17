package ru.practicum.explorewithme.user.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final long id;

    public UserNotFoundException(long id) {
        super("User with id '%d' not exists".formatted(id));
        this.id = id;
    }
}
