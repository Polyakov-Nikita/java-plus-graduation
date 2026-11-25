package ru.practicum.explorewithme.request.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.dto.RequestStatus;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.service.RequestService;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = ACCESS_INTERNAL + URL_REQUESTS)
@RequiredArgsConstructor
@Validated
@Slf4j
@SuppressWarnings("unused")
public class InternalRequestController {
    private final RequestService service;

    @GetMapping("/{" + ID_EVENT + "}")
    public ResponseEntity<List<RequestDto>> getRequests(@PathVariable(name = ID_EVENT) long eventId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getRequestsInternal(eventId));
    }

    @GetMapping(URL_EXISTS)
    public ResponseEntity<Boolean> userAttendEvent(
            @RequestParam(name = PARAM_USER_ID) long userId,
            @RequestParam(name = PARAM_EVENT_ID) long eventId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.userAttendEventInternal(userId, eventId));
    }

    @PutMapping
    public ResponseEntity<List<RequestDto>> changeRequestStatuses(
            @RequestParam(name = PARAM_IDS) List<Long> ids,
            @RequestParam(name = PARAM_STATUS) RequestStatus status
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.changeRequestStatusesInternal(ids, status));
    }
}
