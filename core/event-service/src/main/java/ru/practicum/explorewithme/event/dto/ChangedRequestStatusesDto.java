package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.event.feign.dto.EventRequestDto;

import java.util.List;

@Builder
@Data
public class ChangedRequestStatusesDto {
    List<EventRequestDto> confirmedRequests;
    List<EventRequestDto> rejectedRequests;
}
