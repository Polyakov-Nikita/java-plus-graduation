package ru.practicum.explorewithme.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.*;
import ru.practicum.explorewithme.common.service.EventService;
import ru.practicum.explorewithme.common.service.RequestService;
import ru.practicum.explorewithme.common.service.UserService;
import ru.practicum.explorewithme.request.dto.RequestExtra;
import ru.practicum.explorewithme.request.entity.Request;
import ru.practicum.explorewithme.request.exception.ConfirmedRequestException;
import ru.practicum.explorewithme.request.exception.DuplicatedIdsException;
import ru.practicum.explorewithme.request.exception.RequestNotFoundException;
import ru.practicum.explorewithme.request.exception.UnavailableUpdateRequestException;
import ru.practicum.explorewithme.request.mapper.RequestMapper;
import ru.practicum.explorewithme.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public List<RequestDto> getRequestsToUsersEvent(EventDto event) {
        log.trace("Инициировано получение запросов к событию {}", event);
        List<Request> requests = requestRepository.findByEventId(event.getId());
        log.debug("Получен список запросов {}", requests);
        Map<Long, RequestExtra> extraMap = aggregateExtraMap(requests, event);
        List<RequestDto> result = requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
        result.forEach(request -> addExtra(request, extraMap.get(request.getId())));
        return result;
    }

    private Map<Long, RequestExtra> aggregateExtraMap(List<Request> requests, EventDto event) {
        Map<Long, RequestExtra> result = new HashMap<>();
        Map<Long, UserShortDto> usersMap = userService.getUsersMap(
                requests.stream()
                        .map(Request::getRequesterId)
                        .toList()
        );
        requests.forEach(
                request -> {
                    UserShortDto requester = usersMap.get(request.getRequesterId());
                    RequestExtra extraInfo = RequestExtra.builder()
                            .event(event)
                            .requester(requester)
                            .build();
                    result.put(request.getId(), extraInfo);
                }
        );
        return result;
    }

    @Override
    @Transactional
    public List<RequestDto> changeRequestStatuses(List<Long> requestIds, RequestStatus status) {
        log.trace("Инициировано изменение статусов на {} к списку запросов {}", status, requestIds);
        List<Request> requests = requestRepository.findByIdIn(requestIds);
        checkNonPendingRequests(requests);
        requests.forEach(request -> request.setStatus(status));
        log.debug("Изменен статус у запросов {}", requests);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    private void checkNonPendingRequests(List<Request> requests) {
        Optional<Long> nonPendingRequestId = requests.stream()
                .filter(request -> !request.getStatus().equals(RequestStatus.PENDING))
                .findFirst()
                .map(Request::getId);
        if (nonPendingRequestId.isPresent()) {
            throw new UnavailableUpdateRequestException(nonPendingRequestId.get());
        }
    }

    @Override
    @Transactional
    public RequestDto create(long userId, long eventId) {
        log.trace("Инициировано создание запроса у пользователя с id {} для события с id {}", userId, eventId);
        checkDuplicatedIds(userId, eventId);
        RequestExtra extraInfo = aggregateExtra(userId, eventId);
        Request request = buildRequest(extraInfo);
        Request saved = requestRepository.save(request);
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            eventService.incrementConfirmedRequests(request.getEventId());
        }
        log.debug("Создан запрос {}", saved);
        RequestDto result = RequestMapper.toRequestDto(saved);
        addExtra(result, extraInfo);
        return result;
    }

    private void checkDuplicatedIds(long userId, long eventId) {
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new DuplicatedIdsException(eventId, userId);
        }
    }

    private RequestExtra aggregateExtra(long userId, long eventId) {
        RequestExtra.RequestExtraBuilder extraBuilder = RequestExtra.builder();
        EventDto eventDto = eventService.getEvent(eventId);
        checkEvent(eventDto, userId);
        extraBuilder.event(eventDto);
        UserShortDto userDto = userService.getUser(userId);
        extraBuilder.requester(userDto);
        return extraBuilder.build();
    }

    private void addExtra(RequestDto request, RequestExtra extraInfo) {
        request.setEvent(extraInfo.getEvent());
        request.setRequester(extraInfo.getRequester());
    }

    private void checkEvent(EventDto event, long userId) {
        if (event.getInitiator().getId() == userId) {
            throw new UnavailableUpdateRequestException(event.getId());
        }
        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new UnavailableUpdateRequestException(event.getId());
        }
        if (event.getParticipantLimit() - event.getConfirmedRequests() == 0 && event.getParticipantLimit() != 0) {
            throw new UnavailableUpdateRequestException(event.getId());
        }
    }

    private Request buildRequest(RequestExtra extraInfo) {
        RequestStatus status = chooseRequestStatus(extraInfo.getEvent());
        return Request.builder()
                .created(LocalDateTime.now())
                .eventId(extraInfo.getEvent().getId())
                .requesterId(extraInfo.getRequester().getId())
                .status(status)
                .build();
    }

    private RequestStatus chooseRequestStatus(EventDto eventDto) {
        if (eventDto.getParticipantLimit() == 0 || !eventDto.isRequestModeration()) {
            return RequestStatus.CONFIRMED;
        } else {
            return RequestStatus.PENDING;
        }
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(long userId, long requestId) {
        log.trace("Инициирована отмена запроса с id {} у пользователя с id {}", userId, requestId);
        Request request = findRequest(requestId);
        checkConfirmedStatus(request);
        request.setStatus(RequestStatus.CANCELED);
        Request updated = requestRepository.save(request);
        log.debug("Обновлен запрос {}", updated);
        eventService.decrementConfirmedRequests(request.getEventId());
        RequestExtra extraInfo = aggregateExtra(userId, request.getEventId());
        RequestDto result = RequestMapper.toRequestDto(updated);
        addExtra(result, extraInfo);
        return result;
    }

    private Request findRequest(long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));
    }

    private void checkConfirmedStatus(Request request) {
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConfirmedRequestException(request.getId());
        }
    }

    @Override
    public List<RequestDto> getUserRequests(long requesterId) {
        log.trace("Инициировано получение запросов пользователя с id {}", requesterId);
        UserShortDto user = userService.getUser(requesterId);
        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
        log.debug("Получены запросы пользователя {}", requests);
        Map<Long, RequestExtra> extraMap = aggregateExtraMap(requests, user);
        List<RequestDto> result = requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
        result.forEach(request -> addExtra(request, extraMap.get(request.getId())));
        return result;
    }

    private Map<Long, RequestExtra> aggregateExtraMap(List<Request> requests, UserShortDto requester) {
        Map<Long, RequestExtra> result = new HashMap<>();
        Map<Long, EventDto> eventMap = eventService.getEventMap(
                requests.stream()
                        .map(Request::getEventId)
                        .toList()
        );
        requests.forEach(
                request -> {
                    EventDto event = eventMap.get(request.getEventId());
                    RequestExtra extraInfo = RequestExtra.builder()
                            .event(event)
                            .requester(requester)
                            .build();
                    result.put(request.getId(), extraInfo);
                }
        );
        return result;
    }
}
