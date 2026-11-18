package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;

@Builder
@Data
public class EventExtra {
    private EventCategoryDto category;
    private EventUserDto initiator;
}
