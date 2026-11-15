package ru.practicum.explorewithme.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.EventStatus;
import ru.practicum.explorewithme.common.dto.RequestStatus;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.entity.Request;
import ru.practicum.explorewithme.request.exception.ConfirmedRequestException;
import ru.practicum.explorewithme.request.exception.DuplicatedIdsException;
import ru.practicum.explorewithme.request.exception.RequestNotFoundException;
import ru.practicum.explorewithme.request.exception.UnavailableUpdateRequestException;
import ru.practicum.explorewithme.request.feign.RequestEventClient;
import ru.practicum.explorewithme.request.feign.dto.RequestEventDto;
import ru.practicum.explorewithme.request.mapper.RequestMapper;
import ru.practicum.explorewithme.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestEventClient requestEventClient;

    @Override
    public List<RequestDto> getRequestsInternal(long eventId) {
        log.trace("Инициировано получение запросов к событию с id {}", eventId);
        List<Request> requests = requestRepository.findByEventId(eventId);
        log.debug("Получен список запросов {}", requests);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public List<RequestDto> changeRequestStatusesInternal(List<Long> requestIds, RequestStatus status) {
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
        RequestEventDto event = requestEventClient.getEventById(eventId);
        checkEvent(event, userId);
        Request request = buildRequest(event, userId);
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            requestEventClient.incrementConfirmedRequests(request.getEventId());
        }
        Request saved = requestRepository.save(request);
        log.debug("Создан запрос {}", saved);
        return RequestMapper.toRequestDto(saved);
    }

    private void checkDuplicatedIds(long userId, long eventId) {
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new DuplicatedIdsException(eventId, userId);
        }
    }

    private void checkEvent(RequestEventDto event, long userId) {
        if (event.getInitiatorId() == userId) {
            throw new UnavailableUpdateRequestException(event.getId());
        }
        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new UnavailableUpdateRequestException(event.getId());
        }
        if (event.getParticipantLimit() - event.getConfirmedRequests() == 0 && event.getParticipantLimit() != 0) {
            throw new UnavailableUpdateRequestException(event.getId());
        }
    }

    private Request buildRequest(RequestEventDto event, Long requesterId) {
        RequestStatus status = chooseRequestStatus(event);
        return Request.builder()
                .created(LocalDateTime.now())
                .eventId(event.getId())
                .requesterId(requesterId)
                .status(status)
                .build();
    }

    private RequestStatus chooseRequestStatus(RequestEventDto requestEventDto) {
        if (requestEventDto.getParticipantLimit() == 0 || !requestEventDto.isRequestModeration()) {
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
        requestEventClient.decrementConfirmedRequests(request.getEventId());
        Request updated = requestRepository.save(request);
        log.debug("Обновлен запрос {}", updated);
        return RequestMapper.toRequestDto(updated);
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
        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
        log.debug("Получены запросы пользователя {}", requests);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }
}
