package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.common.dto.EventStatus;

@Builder
@Getter
@ToString
public class InternalEventDto {
    private long id;
    private int confirmedRequests;
    private Long initiatorId;
    private int participantLimit;
    private boolean requestModeration;
    private EventStatus status;
}
