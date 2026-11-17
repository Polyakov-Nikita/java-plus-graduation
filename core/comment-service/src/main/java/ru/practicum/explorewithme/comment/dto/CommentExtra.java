package ru.practicum.explorewithme.comment.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentExtra {
    private Long authorId;
    private Long eventId;
}
