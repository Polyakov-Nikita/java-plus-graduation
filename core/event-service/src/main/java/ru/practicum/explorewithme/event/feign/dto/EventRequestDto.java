package ru.practicum.explorewithme.event.feign.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.common.dto.RequestStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class EventRequestDto {
    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}
