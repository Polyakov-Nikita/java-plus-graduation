package ru.practicum.explorewithme.category.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends RuntimeException {
    private final long id;

    public CategoryNotFoundException(long id) {
        super(String.format("Category with id '%d' not exists", id));
        this.id = id;
    }
}
