package ru.practicum.explorewithme.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.common.dto.EventDto;
import ru.practicum.explorewithme.common.dto.UserShortDto;

@Builder
@Getter
@ToString
public class RequestExtra {
    private EventDto event;
    private UserShortDto requester;
}
