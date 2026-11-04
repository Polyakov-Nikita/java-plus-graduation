package ru.practicum.explorewithme.comment.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {
    private final long id;

    public CommentNotFoundException(long id) {
        super(String.format("Comment with id '%d' not exists", id));
        this.id = id;
    }
}
