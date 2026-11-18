package ru.practicum.explorewithme.request.feign.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.common.dto.EventStatus;

@Builder
@Data
public class RequestEventDto {
    private long id;
    private int confirmedRequests;
    private Long initiatorId;
    private int participantLimit;
    private boolean requestModeration;
    private EventStatus status;
}
