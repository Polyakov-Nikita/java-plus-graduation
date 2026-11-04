package ru.practicum.explorewithme.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class RequestDto {
    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @Setter
    private EventDto event;
    @Setter
    private UserShortDto requester;
    private RequestStatus status;
}
