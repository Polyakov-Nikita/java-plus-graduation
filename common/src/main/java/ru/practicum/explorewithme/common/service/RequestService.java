package ru.practicum.explorewithme.common.service;

import ru.practicum.explorewithme.common.dto.EventDto;
import ru.practicum.explorewithme.common.dto.RequestDto;
import ru.practicum.explorewithme.common.dto.RequestStatus;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequestsToUsersEvent(EventDto event);

    List<RequestDto> changeRequestStatuses(List<Long> requestIds, RequestStatus status);

    RequestDto create(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getUserRequests(long requesterId);
}
