package ru.practicum.explorewithme.category.exception;

import lombok.Getter;

@Getter
public class DuplicatedNameException extends RuntimeException {
    private final String name;

    public DuplicatedNameException(String name) {
        super("Category with name '%s' already exists".formatted(name));
        this.name = name;
    }
}
