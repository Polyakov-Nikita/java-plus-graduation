package ru.practicum.explorewithme.request.mapper;

import ru.practicum.explorewithme.common.dto.RequestDto;
import ru.practicum.explorewithme.request.entity.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
