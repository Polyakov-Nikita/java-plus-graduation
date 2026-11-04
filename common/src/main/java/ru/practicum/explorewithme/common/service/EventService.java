package ru.practicum.explorewithme.common.service;

import ru.practicum.explorewithme.common.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {
    List<EventDto> getEvents(long userId, int from, int size);

    EventDto createEvent(long userId, NewEventDto newEventDto);

    EventDto getEvent(long userId, long eventId);

    EventDto getEvent(long eventId);

    EventDto updateEvent(long userId, long eventId, UserUpdateEventDto userUpdateEventDto);

    ChangedRequestStatusesDto updateRequestStatuses(long userId, long eventId, UpdateRequestStatusDto update);

    List<RequestDto> getRequests(long userId, long eventId);

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

    Boolean existsByCategoryId(long catId);

    void incrementConfirmedRequests(long eventId);

    void decrementConfirmedRequests(long eventId);

    List<EventDto> getEvents(List<Long> ids);

    Map<Long, EventDto> getEventMap(List<Long> ids);
}
