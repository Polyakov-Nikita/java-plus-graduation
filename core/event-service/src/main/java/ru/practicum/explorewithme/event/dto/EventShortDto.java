package ru.practicum.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class EventShortDto {
    private long id;
    private String annotation;
    private EventCategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private EventUserDto initiator;
    private boolean paid;
    private String title;
    private long views;
}
