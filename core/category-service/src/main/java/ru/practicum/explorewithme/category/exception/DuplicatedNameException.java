package ru.practicum.explorewithme.category.exception;

import lombok.Getter;

@Getter
public class DuplicatedNameException extends RuntimeException {
    private final String name;

    public DuplicatedNameException(String name) {
        super(String.format("Category with name '%s' already exists", name));
        this.name = name;
    }
}
