package ru.practicum.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import ru.practicum.explorewithme.common.dto.EventStatus;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;

import java.time.LocalDateTime;

@Builder
@Data
public class EventDto {
    private long id;
    private String annotation;
    @Setter
    private EventCategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Setter
    private EventUserDto initiator;
    @Setter
    private Location location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    @JsonProperty("state")
    private EventStatus status;
    private String title;
    @Setter
    private long views;
}
