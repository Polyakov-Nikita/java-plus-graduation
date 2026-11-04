package ru.practicum.explorewithme.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.common.dto.*;
import ru.practicum.explorewithme.common.service.EventService;
import ru.practicum.explorewithme.common.service.UserService;
import ru.practicum.explorewithme.request.entity.Request;
import ru.practicum.explorewithme.request.exception.ConfirmedRequestException;
import ru.practicum.explorewithme.request.exception.DuplicatedIdsException;
import ru.practicum.explorewithme.request.exception.UnavailableUpdateRequestException;
import ru.practicum.explorewithme.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long REQUEST_ID = 3L;

    @Test
    void create_success() {
        UserShortDto initiator = UserShortDto.builder()
                .id(99L)
                .build();
        EventDto event = EventDto.builder()
                .id(EVENT_ID)
                .initiator(initiator)
                .status(EventStatus.PUBLISHED)
                .participantLimit(10)
                .confirmedRequests(2)
                .requestModeration(false)
                .build();

        UserShortDto user = UserShortDto.builder()
                .id(USER_ID)
                .build();

        Request savedRequest = Request.builder()
                .id(REQUEST_ID)
                .created(LocalDateTime.now())
                .eventId(event.getId())
                .requesterId(user.getId())
                .status(RequestStatus.CONFIRMED)
                .build();

        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.getEvent(anyLong())).thenReturn(event);
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestRepository.save(any(Request.class))).thenReturn(savedRequest);

        RequestDto dto = requestService.create(USER_ID, EVENT_ID);

        assertThat(dto.getId()).isEqualTo(REQUEST_ID);
        assertThat(dto.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(eventService, times(1)).getEvent(EVENT_ID);
        verify(userService, times(1)).getUser(USER_ID);
        verify(requestRepository, times(1)).save(any(Request.class));
        verify(eventService, times(1)).incrementConfirmedRequests(EVENT_ID);
    }

    @Test
    void create_duplicateRequest_throws() {
        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(true);

        assertThatThrownBy(() -> requestService.create(USER_ID, EVENT_ID))
                .isInstanceOf(DuplicatedIdsException.class);

        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(eventService, never()).getEvent(anyLong(), anyLong());
        verify(userService, never()).getUser(anyLong());
    }

    @Test
    void create_unpublishedEvent_throws() {
        UserShortDto initiator = UserShortDto.builder()
                .id(99L)
                .build();
        EventDto event = EventDto.builder()
                .id(EVENT_ID)
                .initiator(initiator)
                .status(EventStatus.PENDING)
                .participantLimit(10)
                .confirmedRequests(0)
                .build();

        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.getEvent(anyLong())).thenReturn(event);

        assertThatThrownBy(() -> requestService.create(USER_ID, EVENT_ID))
                .isInstanceOf(UnavailableUpdateRequestException.class);

        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(eventService, times(1)).getEvent(EVENT_ID);
    }

    @Test
    void create_eventLimitReached_throws() {
        UserShortDto initiator = UserShortDto.builder()
                .id(99L)
                .build();
        EventDto event = EventDto.builder()
                .id(EVENT_ID)
                .status(EventStatus.PUBLISHED)
                .initiator(initiator)
                .participantLimit(5)
                .confirmedRequests(5)
                .requestModeration(true)
                .build();

        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.getEvent(anyLong())).thenReturn(event);

        assertThatThrownBy(() -> requestService.create(USER_ID, EVENT_ID))
                .isInstanceOf(UnavailableUpdateRequestException.class);

        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(eventService, times(1)).getEvent(EVENT_ID);
    }

    @Test
    void cancelRequest_confirmedStatus_throws() {
        Request request = new Request();
        request.setId(REQUEST_ID);
        request.setStatus(RequestStatus.CONFIRMED);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> requestService.cancelRequest(USER_ID, REQUEST_ID))
                .isInstanceOf(ConfirmedRequestException.class)
                .hasMessageContaining("status");

        verify(requestRepository, times(1)).findById(REQUEST_ID);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void cancelRequest_success() {
        UserShortDto initiator = UserShortDto.builder()
                .id(99L)
                .build();

        EventDto event = EventDto.builder()
                .id(EVENT_ID)
                .confirmedRequests(3)
                .initiator(initiator)
                .status(EventStatus.PUBLISHED)
                .build();

        UserShortDto requester = UserShortDto.builder()
                .id(USER_ID)
                .build();

        Request request = Request.builder()
                .id(REQUEST_ID)
                .status(RequestStatus.PENDING)
                .eventId(event.getId())
                .requesterId(requester.getId())
                .build();

        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(eventService.getEvent(anyLong())).thenReturn(event);
        when(userService.getUser(anyLong())).thenReturn(requester);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto dto = requestService.cancelRequest(USER_ID, REQUEST_ID);

        assertThat(dto.getStatus()).isEqualTo(RequestStatus.CANCELED);
        verify(requestRepository, times(1)).findById(REQUEST_ID);
        verify(requestRepository, times(1)).save(request);
        verify(eventService, times(1)).decrementConfirmedRequests(event.getId());
    }
}
