package ru.practicum.explorewithme.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.dto.PublicEventSort;
import ru.practicum.explorewithme.event.dto.EventDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.RatedEventDto;
import ru.practicum.explorewithme.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = URL_EVENTS)
@RequiredArgsConstructor
@Validated
@SuppressWarnings("unused")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(name = PARAM_TEXT, required = false) String text,
            @RequestParam(name = PARAM_CATEGORIES, required = false) List<Long> categories,
            @RequestParam(name = PARAM_PAID, required = false) Boolean paid,
            @RequestParam(name = PARAM_RANGE_START, required = false)
            @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
            @RequestParam(name = PARAM_RANGE_END, required = false)
            @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(name = PARAM_ONLY_AVAILABLE, required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(name = PARAM_SORT, required = false) PublicEventSort sort,
            @RequestParam(name = PARAM_FROM, required = false, defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(name = PARAM_SIZE, required = false, defaultValue = "10")
            @Positive int size,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getPublishedEvents(
                        text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable,
                        sort == null ? PublicEventSort.EVENT_DATE : sort,
                        from,
                        size,
                        request.getRemoteAddr(),
                        request.getRequestURI()
                ));
    }

    @GetMapping("/{" + ID_EVENT + "}")
    public ResponseEntity<EventDto> getEvent(
            @RequestHeader(name = HEADER_USER_ID) long userId,
            @PathVariable(name = ID_EVENT) long eventId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getPublishedEvent(userId, eventId));
    }

    @GetMapping(URL_RECOMMENDATIONS)
    public ResponseEntity<List<RatedEventDto>> getRecommendations(@RequestHeader(name = HEADER_USER_ID) long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getRecommendations(userId));
    }

    @PutMapping("/{" + ID_EVENT + "}" + URL_LIKE)
    public ResponseEntity<Void> addLike(
            @RequestHeader(name = HEADER_USER_ID) long userId,
            @PathVariable(name = ID_EVENT) long eventId
    ) {
        eventService.addLike(userId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
