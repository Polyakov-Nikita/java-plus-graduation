package ru.practicum.explorewithme.compilations.exception;

import lombok.Getter;

@Getter
public class CompilationNotFoundException extends RuntimeException {
    private final long id;

    public CompilationNotFoundException(long id) {
        super("Compilation with id '%d' not exists".formatted(id));
        this.id = id;
    }
}
