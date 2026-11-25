package ru.practicum.explorewithme.event.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RatedEventDto {
    private long id;
    private double rating;
}
