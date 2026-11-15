package ru.practicum.explorewithme.request.mapper;

import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.entity.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getStatus())
                .build();
    }
}
