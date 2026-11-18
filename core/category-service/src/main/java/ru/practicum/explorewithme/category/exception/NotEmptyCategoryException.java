package ru.practicum.explorewithme.category.exception;

import lombok.Getter;

@Getter
public class NotEmptyCategoryException extends RuntimeException {
    private final long categoryId;

    public NotEmptyCategoryException(long categoryId) {
        super("Category with id='%d' is not empty".formatted(categoryId));
        this.categoryId = categoryId;
    }
}
