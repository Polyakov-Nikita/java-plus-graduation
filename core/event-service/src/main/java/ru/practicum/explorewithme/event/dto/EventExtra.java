package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;

@Builder
@Getter
@ToString
public class EventExtra {
    private EventCategoryDto category;
    private EventUserDto initiator;
}
