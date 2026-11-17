package ru.practicum.explorewithme.comment.feign.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.common.dto.EventStatus;

@Builder
@Data
public class CommentEventDto {
    private long id;
    private int confirmedRequests;
    private Long initiatorId;
    private int participantLimit;
    private boolean requestModeration;
    private EventStatus status;
}
