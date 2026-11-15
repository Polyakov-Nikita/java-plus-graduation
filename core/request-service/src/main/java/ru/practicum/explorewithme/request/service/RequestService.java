package ru.practicum.explorewithme.request.service;

import ru.practicum.explorewithme.common.dto.RequestStatus;
import ru.practicum.explorewithme.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequestsInternal(long eventId);

    List<RequestDto> changeRequestStatusesInternal(List<Long> requestIds, RequestStatus status);

    RequestDto create(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getUserRequests(long requesterId);
}
