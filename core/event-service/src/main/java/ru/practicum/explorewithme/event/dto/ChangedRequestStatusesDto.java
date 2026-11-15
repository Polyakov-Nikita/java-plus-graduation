package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.event.feign.dto.EventRequestDto;

import java.util.List;

@Builder
@Getter
@ToString
public class ChangedRequestStatusesDto {
    List<EventRequestDto> confirmedRequests;
    List<EventRequestDto> rejectedRequests;
}
