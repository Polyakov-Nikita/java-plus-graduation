package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.common.dto.*;
import ru.practicum.explorewithme.common.pagination.OffsetPageRequest;
import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.entity.Event;
import ru.practicum.explorewithme.event.entity.LocationEmbeddable;
import ru.practicum.explorewithme.event.exception.EarlyDateException;
import ru.practicum.explorewithme.event.exception.EventNotFoundException;
import ru.practicum.explorewithme.event.exception.UnavailableUpdateEventException;
import ru.practicum.explorewithme.event.exception.WrongDateIntervalException;
import ru.practicum.explorewithme.event.feign.EventCategoryClient;
import ru.practicum.explorewithme.event.feign.EventRequestClient;
import ru.practicum.explorewithme.event.feign.EventUserClient;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;
import ru.practicum.explorewithme.event.feign.dto.EventRequestDto;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;
import ru.practicum.explorewithme.event.mapper.EventMapper;
import ru.practicum.explorewithme.event.mapper.LocationMapper;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.event.repository.specification.AdminEventSearchSpecification;
import ru.practicum.explorewithme.event.repository.specification.UsersEventSearchSpecifications;
import ru.practicum.explorewithme.hit.EndpointHitRequest;
import ru.practicum.explorewithme.stats.ViewStatsResponse;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.URL_EVENTS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {
    private static final String APP_NAME = "event-service";
    private static final int ADMIN_MIN_OFFSET = 1;

    private final EventRepository eventRepository;
    private final EventCategoryClient eventCategoryClient;
    private final EventUserClient eventUserClient;
    private final EventRequestClient eventRequestClient;
    private final StatsClient statsClient;

    @Override
    public List<EventDto> getEvents(long userId, int from, int size) {
        log.debug("Инициировано получение событий пользователем с id {}", userId);
        Pageable pageable = new OffsetPageRequest(from, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        log.debug("Получен список событий {}", events);
        if (events.isEmpty()) {
            return List.of();
        }
        return getEventsWithStats(events, statsClient);
    }

    private List<EventDto> getEventsWithStats(List<Event> events, StatsClient statsClient) {
        Map<String, Long> viewsByUri = getStats(statsClient, events);
        List<EventDto> mappedEvents = events.stream()
                .map(event -> {
                    EventCategoryDto category = eventCategoryClient.getCategoryById(event.getCategoryId());
                    EventUserDto initiator = eventUserClient.getUserById(event.getInitiatorId());
                    Location location = LocationMapper.toLocation(event.getLocation());
                    EventDto eventDto = EventMapper.toEventDto(event);
                    setNestedClassesValues(eventDto, category, initiator, location);
                    return eventDto;
                })
                .toList();

        mappedEvents.forEach(eventDto -> {
            long views = getViews(eventDto, viewsByUri);
            eventDto.setViews(views);
        });
        log.debug("К событиям {} добавлено количество просмотров", mappedEvents);
        return mappedEvents;
    }

    private Map<String, Long> getStats(StatsClient statsClient, List<Event> events) {
        LocalDateTime start = getStartPoint(events);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = getUris(events);
        List<ViewStatsResponse> stats = statsClient.getStatistics(start, end, uris, true);
        Map<String, Long> viewsByUri = new HashMap<>();
        stats.forEach(stat -> viewsByUri.put(stat.getUri(), stat.getHits()));
        return viewsByUri;
    }

    private LocalDateTime getStartPoint(List<Event> events) {
        return events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No events were found"));
    }

    private List<String> getUris(List<Event> events) {
        return events.stream()
                .map(event -> URL_EVENTS + "/" + event.getId())
                .toList();
    }

    private void setNestedClassesValues(EventDto event,
                                        EventCategoryDto category,
                                        EventUserDto initiator,
                                        Location location) {
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setLocation(location);
    }

    private long getViews(EventDto event, Map<String, Long> viewsByUri) {
        return viewsByUri.getOrDefault(URL_EVENTS + "/" + event.getId(), 0L);
    }

    @Override
    @Transactional
    public EventDto createEvent(long userId, NewEventDto newEventDto) {
        log.debug("Инициировано создание события {} пользователем с id {}", newEventDto, userId);
        EventExtra extraInfo = aggregateExtra(userId, newEventDto);
        Event event = EventMapper.toEvent(newEventDto);
        addExtra(event, extraInfo);
        setParamsOnCreation(event, newEventDto);
        Event saved = eventRepository.save(event);
        log.debug("Создано событие {}", saved);
        EventDto result = EventMapper.toEventDto(saved);
        addExtra(result, extraInfo);
        log.debug("Событие преобразовано в DTO {}", result);
        return result;
    }

    private EventExtra aggregateExtra(long userId, NewEventDto newEventDto) {
        EventExtra.EventExtraBuilder extraBuilder = EventExtra.builder();
        EventCategoryDto categoryInfo = eventCategoryClient.getCategoryById(newEventDto.getCategory());
        extraBuilder.category(categoryInfo);
        EventUserDto userInfo = eventUserClient.getUserById(userId);
        extraBuilder.initiator(userInfo);
        return extraBuilder.build();
    }

    private void addExtra(Event event, EventExtra extraInfo) {
        event.setCategoryId(extraInfo.getCategory().getId());
        event.setInitiatorId(extraInfo.getInitiator().getId());
    }

    private void addExtra(EventDto event, EventExtra extraInfo) {
        event.setCategory(extraInfo.getCategory());
        event.setInitiator(extraInfo.getInitiator());
    }

    private void setParamsOnCreation(Event event, NewEventDto newEventDto) {
        event.setCreatedOn(LocalDateTime.now());
        event.setStatus(EventStatus.PENDING);
        event.setRequestModeration(newEventDto.getRequestModerationOrDefault());
    }

    @Override
    public EventDto getEvent(long userId, long eventId) {
        log.debug("Инициировано получение события {} пользователем с id {}", eventId, userId);
        Event event = findEvent(eventId, userId);
        log.debug("Получено событие {}", event);
        return getEventsWithStats(List.of(event), statsClient).getFirst();
    }

    private Event findEvent(long eventId, long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @Override
    @Transactional
    public EventDto updateEvent(long userId, long eventId, UserUpdateEventDto update) {
        log.debug("Инициировано обновление события с id {} пользователем с id {}, изменения - {}", eventId, userId, update);
        Event event = findEvent(eventId, userId);
        checkEventNotCanceled(event);
        EventStatus status = changeEventStatus(update);
        updateEventFields(event, update, status);
        Event updatedEvent = eventRepository.save(event);
        log.debug("Обновлено событие {}", updatedEvent);
        return getEventsWithStats(List.of(updatedEvent), statsClient).getFirst();
    }

    private void checkEventNotCanceled(Event event) {
        if (!event.getStatus().equals(EventStatus.CANCELED) && !event.getStatus().equals(EventStatus.PENDING)) {
            throw new UnavailableUpdateEventException(event.getId());
        }
    }

    private EventStatus changeEventStatus(UserUpdateEventDto update) {
        EventStatus status;

        if (update.getStatus() != null && update.getStatus().equals(UserEventUpdateAction.CANCEL_REVIEW)) {
            status = EventStatus.CANCELED;
        } else {
            status = EventStatus.PENDING;
        }

        return status;
    }

    @Override
    public List<EventRequestDto> getRequests(long userId, long eventId) {
        log.debug("Инициировано получение запроса пользователем с id {} к событию с id {}", userId, eventId);
        checkEventExistence(userId, eventId);
        Event event = findEvent(eventId);
        return eventRequestClient.getRequests(event.getId());
    }

    private void checkEventExistence(long userId, long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventNotFoundException(eventId);
        }
    }

    @Override
    @Transactional
    public ChangedRequestStatusesDto updateRequestStatuses(long userId, long eventId, UpdateRequestStatusDto update) {
        log.debug("Инициировано обновление статусов запросов пользователем с id {} к событию с id {}, изменения - {}", userId, eventId, update);
        Event event = findEvent(eventId, userId);
        //Обработка ситуации, когда не установлено ограничение по количеству участников
        //или запрос не требует модерации
        if ((event.getParticipantLimit() == 0 || !event.isRequestModeration()) && update.getStatus().equals(RequestStatus.CONFIRMED)) {
            return confirmRequestsIfNoDemandsMade(event, update);
        }
        int requestsAvailableToConfirm = event.getParticipantLimit() - event.getConfirmedRequests();
        if (requestsAvailableToConfirm == 0) {
            throw new UnavailableUpdateEventException(event.getId());
        }
        //Обработка случая подтверждения запросов
        if (update.getStatus().equals(RequestStatus.CONFIRMED)) {
            return processRequestsWithActionStatusConfirmed(event, update, requestsAvailableToConfirm);
        }
        //Обработка случая отклонения запросов
        return processRequestsWithActionStatusRejected(update);
    }

    @Override
    public List<EventShortDto> getPublishedEvents(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  boolean onlyAvailable,
                                                  PublicEventSort sort,
                                                  int from,
                                                  int size,
                                                  String ip,
                                                  String uri) {
        log.debug("Инициировано получение опубликованных событий");
        checkDateRange(rangeStart, rangeEnd);
        saveHit(ip, uri);

        LocalDateTime start = rangeStart;
        if (rangeStart == null && rangeEnd == null) {
            start = LocalDateTime.now();
        }

        Specification<Event> specification = Specification.<Event>unrestricted()
                .and(UsersEventSearchSpecifications.hasStatus(EventStatus.PUBLISHED))
                .and(UsersEventSearchSpecifications.textContains(text))
                .and(UsersEventSearchSpecifications.categoryIn(categories))
                .and(UsersEventSearchSpecifications.paidEquals(paid))
                .and(UsersEventSearchSpecifications.eventDateFrom(start))
                .and(UsersEventSearchSpecifications.eventDateTo(rangeEnd))
                .and(UsersEventSearchSpecifications.onlyAvailable(onlyAvailable));

        if (sort == PublicEventSort.VIEWS) {
            List<Event> events = eventRepository.findAll(specification);

            if (events.isEmpty()) {
                return List.of();
            }

            List<EventShortDto> dtos = getEventsWithStats(events, statsClient).stream()
                    .map(EventMapper::toEventShortDto)
                    .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                    .toList();
            log.debug("Получен список опубликованных событий {}", dtos);
            return getPage(dtos, from, size);
        }

        Pageable pageable = new OffsetPageRequest(from, size, Sort.by("eventDate").ascending());
        Page<Event> eventPage = eventRepository.findAll(specification, pageable);
        List<Event> events = eventPage.getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<EventShortDto> dtos = getEventsWithStats(events, statsClient).stream()
                .map(EventMapper::toEventShortDto)
                .toList();
        log.debug("Получен список опубликованных событий {}", dtos);
        return dtos;
    }

    @Override
    public EventDto getPublishedEvent(long eventId, String ip, String uri) {
        log.debug("Инициировано получение опубликованного события с id {}", eventId);
        saveHit(ip, uri);
        Event event = findEvent(eventId, EventStatus.PUBLISHED);
        return getEventsWithStats(List.of(event), statsClient).getFirst();
    }

    private Event findEvent(long eventId, EventStatus status) {
        return eventRepository.findByIdAndStatus(eventId, status)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @Override
    public List<EventDto> searchEvents(List<Long> users,
                                       List<String> states,
                                       List<Long> categories,
                                       String rangeStart,
                                       String rangeEnd,
                                       int from,
                                       int size) {
        log.debug("Инициирован поиск событий");
        Specification<Event> spec = new AdminEventSearchSpecification(users, states, categories, rangeStart, rangeEnd);
        Pageable pageable = new OffsetPageRequest(from, size);
        Page<Event> eventPage = eventRepository.findAll(spec, pageable);
        List<Event> events = eventPage.getContent();
        log.debug("Получен список событий {}", events);
        if (events.isEmpty()) {
            return List.of();
        }

        return getEventsWithStats(events, statsClient);
    }

    @Override
    @Transactional
    public EventDto updateEvent(long eventId, AdminUpdateEventDto update) {
        log.debug("Инициировано обновление события с id {} администратором, изменения - {}", eventId, update);
        Event event = findEvent(eventId);
        EventStatus status = changeEventStatus(event, update);

        //Валидация даты события проходит после изменения статуса для случая изменения данных о событии одновременно с публикацией
        if (event.getPublishedOn() != null) {
            if (update.getEventDate() != null && !update.getEventDate().isAfter(event.getPublishedOn().plusHours(ADMIN_MIN_OFFSET))) {
                throw new EarlyDateException(ADMIN_MIN_OFFSET, event.getEventDate());
            }
            //Обработка случая, если событие изменяется, но не опубликовано
        } else {
            if (update.getEventDate() != null && !update.getEventDate().isAfter(event.getCreatedOn().plusHours(ADMIN_MIN_OFFSET))) {
                throw new EarlyDateException(ADMIN_MIN_OFFSET, event.getEventDate());
            }
        }

        updateEventFields(event, update, status);
        Event updatedEvent = eventRepository.save(event);
        log.debug("Обновлено событие {}", updatedEvent);
        return getEventsWithStats(List.of(updatedEvent), statsClient).getFirst();
    }

    private EventStatus changeEventStatus(Event event, AdminUpdateEventDto update) {
        EventStatus status;

        if (update.getStatus() != null && update.getStatus().equals(AdminEventUpdateAction.PUBLISH_EVENT)) {
            checkEventNotPublished(event);
            status = EventStatus.PUBLISHED;
            event.setPublishedOn(LocalDateTime.now());
        } else if (update.getStatus() != null && update.getStatus().equals(AdminEventUpdateAction.REJECT_EVENT)) {
            checkEventNotPublished(event);
            status = EventStatus.CANCELED;
        } else {
            status = event.getStatus();
        }

        return status;
    }

    private void checkEventNotPublished(Event event) {
        if (!event.getStatus().equals(EventStatus.PENDING)) {
            throw new UnavailableUpdateEventException(event.getId());
        }
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private <T> List<T> getPage(List<T> source, int from, int size) {
        if (from >= source.size()) {
            return List.of();
        }

        int toIndex = Math.min(from + size, source.size());
        return source.subList(from, toIndex);
    }

    private void saveHit(String ip, String uri) {
        statsClient.addStatistics(EndpointHitRequest.builder()
                .app(APP_NAME)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build());
    }

    private void checkDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new WrongDateIntervalException("rangeStart must be before rangeEnd");
        }
    }

    private void updateEventFields(Event event, EventUpdateCommon update, EventStatus status) {
        if (update.getAnnotation() != null && !update.getAnnotation().isEmpty()) {
            event.setAnnotation(update.getAnnotation());
        }
        if (update.getDescription() != null && !update.getDescription().isEmpty()) {
            event.setDescription(update.getDescription());
        }
        if (update.getEventDate() != null) {
            event.setEventDate(update.getEventDate());
        }
        if (update.getLocation() != null) {
            LocationEmbeddable location = LocationMapper.toLocationEmbeddable(update.getLocation());
            event.setLocation(location);
        }

        if (update.getPaid() != null) {
            event.setPaid(update.getPaid());
        }

        if (update.getAnnotation() != null && !update.getAnnotation().isEmpty()) {
            event.setAnnotation(update.getAnnotation());
        }

        if (update.getParticipantLimit() != null) {
            event.setParticipantLimit(update.getParticipantLimit());
        }

        if (update.getRequestModeration() != null) {
            event.setRequestModeration(update.getRequestModeration());
        }

        event.setStatus(status);

        if (update.getTitle() != null && !update.getTitle().isEmpty()) {
            event.setTitle(update.getTitle());
        }
    }

    private ChangedRequestStatusesDto changeRequestStatuses(Event event, List<Long> requestsToConfirm, List<Long> requestsToReject) {
        List<EventRequestDto> confirmedRequests;
        if (!requestsToConfirm.isEmpty()) {
            confirmedRequests = eventRequestClient.changeRequestStatuses(requestsToConfirm, RequestStatus.CONFIRMED);
        } else {
            confirmedRequests = List.of();
        }

        List<EventRequestDto> rejectedRequests;
        if (!requestsToReject.isEmpty()) {
            rejectedRequests = eventRequestClient.changeRequestStatuses(requestsToReject, RequestStatus.REJECTED);
        } else {
            rejectedRequests = List.of();
        }

        //Обновляем количество свободных мест
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
        eventRepository.save(event);
        log.debug("Обновлены статусы запросов: подтвержденные - {}, отклоненные - {}", confirmedRequests, rejectedRequests);
        return ChangedRequestStatusesDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private ChangedRequestStatusesDto confirmRequestsIfNoDemandsMade(Event event, UpdateRequestStatusDto update) {
        List<Long> requestIds = update.getRequestIds();
        return changeRequestStatuses(event, requestIds, List.of());
    }

    private ChangedRequestStatusesDto processRequestsWithActionStatusConfirmed(Event event,
                                                                               UpdateRequestStatusDto update,
                                                                               int requestsAvailableToConfirm) {
        //Разделяем запросы на те, которые можем одобрить и отклонить, исходя из количества доступных мест
        List<Long> requestsToConfirm = update.getRequestIds().stream()
                .limit(requestsAvailableToConfirm)
                .toList();

        List<Long> requestsToReject = update.getRequestIds().stream()
                .skip(requestsAvailableToConfirm)
                .toList();

        return changeRequestStatuses(event, requestsToConfirm, requestsToReject);
    }

    private ChangedRequestStatusesDto processRequestsWithActionStatusRejected(UpdateRequestStatusDto update) {
        List<EventRequestDto> rejectedRequests = eventRequestClient.changeRequestStatuses(update.getRequestIds(), RequestStatus.REJECTED);

        return ChangedRequestStatusesDto.builder()
                .confirmedRequests(List.of())
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    public InternalEventDto getEventInternal(long eventId) {
        log.debug("Инициировано получение события с id {}", eventId);
        Event event = findEvent(eventId);
        log.debug("Получено событие {}", event);
        return EventMapper.toInternalEventDto(event);
    }

    @Override
    public Boolean existsByCategoryIdInternal(long catId) {
        return eventRepository.existsByCategoryId(catId);
    }

    @Override
    public List<InternalEventDto> getEventsByIdsInternal(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Event> result = eventRepository.findByIdIn(ids);
        checkEventsPresent(ids, result);
        return result.stream()
                .map(EventMapper::toInternalEventDto)
                .toList();
    }

    private void checkEventsPresent(List<Long> ids, List<Event> events) {
        Set<Long> foundIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
        List<Long> missingIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new EventNotFoundException(missingIds.getFirst());
        }
    }

    @Override
    @Transactional
    public void incrementConfirmedRequestsInternal(long eventId) {
        log.debug("Инициировано увеличение на 1 числа подтвержденных запросов к событию с id {}", eventId);
        Event event = findEvent(eventId);
        int confirmedRequests = event.getConfirmedRequests() + 1;
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void decrementConfirmedRequestsInternal(long eventId) {
        log.debug("Инициировано уменьшение на 1 числа подтвержденных запросов к событию с id {}", eventId);
        Event event = findEvent(eventId);
        int confirmedRequests = event.getConfirmedRequests() - 1;
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
    }
}
