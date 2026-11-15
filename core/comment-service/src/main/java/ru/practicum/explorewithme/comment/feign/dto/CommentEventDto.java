package ru.practicum.explorewithme.comment.feign.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.common.dto.EventStatus;

@Builder
@Getter
@ToString
public class CommentEventDto {
    private long id;
    private int confirmedRequests;
    private Long initiatorId;
    private int participantLimit;
    private boolean requestModeration;
    private EventStatus status;
}
