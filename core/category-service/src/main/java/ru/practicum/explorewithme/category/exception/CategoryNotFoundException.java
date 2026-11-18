package ru.practicum.explorewithme.category.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends RuntimeException {
    private final long id;

    public CategoryNotFoundException(long id) {
        super("Category with id '%d' not exists".formatted(id));
        this.id = id;
    }
}
