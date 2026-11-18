package ru.practicum.explorewithme.comment.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {
    private final long id;

    public CommentNotFoundException(long id) {
        super("Comment with id '%d' not exists".formatted(id));
        this.id = id;
    }
}
