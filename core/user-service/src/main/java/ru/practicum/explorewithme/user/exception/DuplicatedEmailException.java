package ru.practicum.explorewithme.user.exception;

import lombok.Getter;

@Getter
public class DuplicatedEmailException extends RuntimeException {
    private final String email;

    public DuplicatedEmailException(String email) {
        super("User with email '%s' already exists".formatted(email));
        this.email = email;
    }
}
