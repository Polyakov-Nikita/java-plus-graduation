package ru.practicum.explorewithme.comment.exception;

import lombok.Getter;

@Getter
public class UnavailableUpdateCommentException extends RuntimeException {
    private final long id;

    public UnavailableUpdateCommentException(long id) {
        super("Update to comment with id '%d' is unavailable".formatted(id));
        this.id = id;
    }
}
