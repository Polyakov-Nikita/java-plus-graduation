package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.common.dto.PublicEventSort;
import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.feign.dto.EventRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventDto> getEvents(long userId, int from, int size);

    EventDto createEvent(long userId, NewEventDto newEventDto);

    EventDto getEvent(long userId, long eventId);

    EventDto updateEvent(long userId, long eventId, UserUpdateEventDto userUpdateEventDto);

    ChangedRequestStatusesDto updateRequestStatuses(long userId, long eventId, UpdateRequestStatusDto update);

    List<EventRequestDto> getRequests(long userId, long eventId);

    List<EventShortDto> getPublishedEvents(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           boolean onlyAvailable,
                                           PublicEventSort sort,
                                           int from,
                                           int size,
                                           String ip,
                                           String uri);

    EventDto getPublishedEvent(long eventId, String ip, String uri);

    List<EventDto> searchEvents(List<Long> users,
                                List<String> states,
                                List<Long> categories,
                                String rangeStart,
                                String rangeEnd,
                                int from,
                                int size);

    EventDto updateEvent(long eventId, AdminUpdateEventDto adminUpdateEventDto);

    InternalEventDto getEventInternal(long eventId);

    Boolean existsByCategoryIdInternal(long catId);

    List<InternalEventDto> getEventsByIdsInternal(List<Long> ids);

    void incrementConfirmedRequestsInternal(long eventId);

    void decrementConfirmedRequestsInternal(long eventId);
}
