package ru.practicum.explorewithme.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.stats.proto.message.ActionTypeProto;
import ru.practicum.explorewithme.common.dto.EventStatus;
import ru.practicum.explorewithme.common.dto.RequestStatus;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.entity.Request;
import ru.practicum.explorewithme.request.exception.ConfirmedRequestException;
import ru.practicum.explorewithme.request.exception.DuplicatedIdsException;
import ru.practicum.explorewithme.request.exception.UnavailableUpdateRequestException;
import ru.practicum.explorewithme.request.feign.RequestEventClient;
import ru.practicum.explorewithme.request.feign.dto.RequestEventDto;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.stats.client.RecommendationsClient;

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
    private RequestEventClient requestEventClient;
    @Mock
    private RecommendationsClient recommendationsClient;


    @InjectMocks
    private RequestServiceImpl requestService;

    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long REQUEST_ID = 3L;

    @Test
    void create_success() {
        RequestEventDto event = RequestEventDto.builder()
                .id(EVENT_ID)
                .initiatorId(99L)
                .status(EventStatus.PUBLISHED)
                .participantLimit(10)
                .confirmedRequests(2)
                .requestModeration(false)
                .build();

        Request savedRequest = Request.builder()
                .id(REQUEST_ID)
                .created(LocalDateTime.now())
                .eventId(EVENT_ID)
                .requesterId(USER_ID)
                .status(RequestStatus.CONFIRMED)
                .build();

        doNothing().when(recommendationsClient).collect(anyLong(), anyLong(), any(ActionTypeProto.class));
        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(requestEventClient.getEventById(anyLong())).thenReturn(event);
        when(requestRepository.save(any(Request.class))).thenReturn(savedRequest);

        RequestDto dto = requestService.create(USER_ID, EVENT_ID);

        assertThat(dto.getId()).isEqualTo(REQUEST_ID);
        assertThat(dto.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(requestEventClient, times(1)).getEventById(EVENT_ID);
        verify(requestRepository, times(1)).save(any(Request.class));
        verify(requestEventClient, times(1)).incrementConfirmedRequests(EVENT_ID);
    }

    @Test
    void create_duplicateRequest_throws() {
        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(true);

        assertThatThrownBy(() -> requestService.create(USER_ID, EVENT_ID))
                .isInstanceOf(DuplicatedIdsException.class);

        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(requestEventClient, never()).getEventById(anyLong());
    }

    @Test
    void create_unpublishedEvent_throws() {
        RequestEventDto event = RequestEventDto.builder()
                .id(EVENT_ID)
                .initiatorId(99L)
                .status(EventStatus.PENDING)
                .participantLimit(10)
                .confirmedRequests(0)
                .build();

        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(requestEventClient.getEventById(anyLong())).thenReturn(event);

        assertThatThrownBy(() -> requestService.create(USER_ID, EVENT_ID))
                .isInstanceOf(UnavailableUpdateRequestException.class);

        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(requestEventClient, times(1)).getEventById(EVENT_ID);
    }

    @Test
    void create_eventLimitReached_throws() {
        RequestEventDto event = RequestEventDto.builder()
                .id(EVENT_ID)
                .status(EventStatus.PUBLISHED)
                .initiatorId(99L)
                .participantLimit(5)
                .confirmedRequests(5)
                .requestModeration(true)
                .build();

        when(requestRepository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(requestEventClient.getEventById(anyLong())).thenReturn(event);

        assertThatThrownBy(() -> requestService.create(USER_ID, EVENT_ID))
                .isInstanceOf(UnavailableUpdateRequestException.class);

        verify(requestRepository, times(1)).existsByEventIdAndRequesterId(EVENT_ID, USER_ID);
        verify(requestEventClient, times(1)).getEventById(EVENT_ID);
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
        RequestEventDto event = RequestEventDto.builder()
                .id(EVENT_ID)
                .confirmedRequests(3)
                .initiatorId(99L)
                .status(EventStatus.PUBLISHED)
                .build();
        Request request = Request.builder()
                .id(REQUEST_ID)
                .status(RequestStatus.PENDING)
                .eventId(event.getId())
                .requesterId(USER_ID)
                .build();

        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto dto = requestService.cancelRequest(USER_ID, REQUEST_ID);

        assertThat(dto.getStatus()).isEqualTo(RequestStatus.CANCELED);
        verify(requestRepository, times(1)).findById(REQUEST_ID);
        verify(requestRepository, times(1)).save(request);
        verify(requestEventClient, times(1)).decrementConfirmedRequests(event.getId());
    }
}
