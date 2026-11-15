package ru.practicum.explorewithme.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.InternalEventDto;
import ru.practicum.explorewithme.event.service.EventService;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = ACCESS_INTERNAL + URL_EVENTS)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class InternalEventController {
    private final EventService eventService;

    @GetMapping("/{" + ID_EVENT + "}")
    public ResponseEntity<InternalEventDto> getEventById(@PathVariable(name = ID_EVENT) long eventId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getEventInternal(eventId));
    }

    @GetMapping(URL_EXISTS + URL_CATEGORIES + "/{" + ID_CATEGORY + "}")
    public ResponseEntity<Boolean> existsByCategoryId(@PathVariable(name = ID_CATEGORY) long catId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.existsByCategoryIdInternal(catId));
    }

    @GetMapping
    public ResponseEntity<List<InternalEventDto>> getEventsByIds(@RequestParam(name = PARAM_IDS) List<Long> ids) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getEventsByIdsInternal(ids));
    }

    @PutMapping("/{" + ID_EVENT + "}" + URL_REQUESTS + URL_INCREMENT)
    public ResponseEntity<Void> incrementConfirmedRequests(@PathVariable(name = ID_EVENT) long eventId) {
        eventService.incrementConfirmedRequestsInternal(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @PutMapping("/{" + ID_EVENT + "}" + URL_REQUESTS + URL_DECREMENT)
    public ResponseEntity<Void> decrementConfirmedRequests(@PathVariable(name = ID_EVENT) long eventId) {
        eventService.decrementConfirmedRequestsInternal(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
