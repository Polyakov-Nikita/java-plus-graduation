package ru.practicum.explorewithme.event.feign.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventCategoryDto {
    private long id;
    private String name;
}
