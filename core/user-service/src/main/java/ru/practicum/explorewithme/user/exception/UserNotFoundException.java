package ru.practicum.explorewithme.user.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final long id;

    public UserNotFoundException(long id) {
        super(String.format("User with id '%d' not exists", id));
        this.id = id;
    }
}
