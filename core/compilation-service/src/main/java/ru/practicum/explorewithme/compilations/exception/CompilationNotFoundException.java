package ru.practicum.explorewithme.compilations.exception;

import lombok.Getter;

@Getter
public class CompilationNotFoundException extends RuntimeException {
    private final long id;

    public CompilationNotFoundException(long id) {
        super(String.format("Compilation with id '%d' not exists", id));
        this.id = id;
    }
}
