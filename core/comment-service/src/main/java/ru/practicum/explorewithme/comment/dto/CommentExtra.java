package ru.practicum.explorewithme.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CommentExtra {
    private Long authorId;
    private Long eventId;
}
