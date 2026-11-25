package ru.practicum.explorewithme.event.mapper;

import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.entity.Event;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;

public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocationEmbeddable(newEventDto.getLocation()))
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toEventCategoryDto(event.getCategoryId()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(toEventUserDto(event.getInitiatorId()))
                .location(LocationMapper.toLocation(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .status(event.getStatus())
                .title(event.getTitle())
                .build();
    }

    private static EventCategoryDto toEventCategoryDto(long catId) {
        return EventCategoryDto.builder()
                .id(catId)
                .build();
    }

    private static EventUserDto toEventUserDto(long userId) {
        return EventUserDto.builder()
                .id(userId)
                .build();
    }

    public static EventShortDto toEventShortDto(EventDto eventDto) {
        return EventShortDto.builder()
                .id(eventDto.getId())
                .annotation(eventDto.getAnnotation())
                .category(eventDto.getCategory())
                .confirmedRequests(eventDto.getConfirmedRequests())
                .eventDate(eventDto.getEventDate())
                .initiator(eventDto.getInitiator())
                .paid(eventDto.isPaid())
                .title(eventDto.getTitle())
                .build();
    }

    public static InternalEventDto toInternalEventDto(Event event) {
        return InternalEventDto.builder()
                .id(event.getId())
                .confirmedRequests(event.getConfirmedRequests())
                .initiatorId(event.getInitiatorId())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .status(event.getStatus())
                .build();
    }

    public static RatedEventDto toRatedEventDto(Event event) {
        return RatedEventDto.builder()
                .id(event.getId())
                .build();
    }
}
