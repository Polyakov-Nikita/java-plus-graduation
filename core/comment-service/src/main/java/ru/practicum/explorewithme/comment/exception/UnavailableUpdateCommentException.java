package ru.practicum.explorewithme.comment.exception;

import lombok.Getter;

@Getter
public class UnavailableUpdateCommentException extends RuntimeException {
    private final long id;

    public UnavailableUpdateCommentException(long id) {
        super(String.format("Update to comment with id '%d' is unavailable", id));
        this.id = id;
    }
}
