package ru.practicum.explorewithme.common.dto;

import java.time.LocalDateTime;

public interface EventUpdateCommon {
    String getTitle();

    String getAnnotation();

    String getDescription();

    Long getCategory();

    LocalDateTime getEventDate();

    Location getLocation();

    Boolean getPaid();

    Integer getParticipantLimit();

    Boolean getRequestModeration();
}
