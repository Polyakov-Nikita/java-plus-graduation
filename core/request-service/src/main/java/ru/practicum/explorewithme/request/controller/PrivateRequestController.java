package ru.practicum.explorewithme.request.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.service.RequestService;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = ACCESS_PRIVATE + "/{" + ID_USER + "}" + URL_REQUESTS)
@RequiredArgsConstructor
@Validated
@SuppressWarnings("unused")
public class PrivateRequestController {
    private final RequestService service;

    @PostMapping
    public ResponseEntity<RequestDto> addRequest(@PathVariable @NotNull @Positive Long userId,
                                                 @RequestParam(name = "eventId") @NotNull @Positive Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(userId, eventId));
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@PathVariable @NotNull @Positive Long userId) {
        return service.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequest(@PathVariable @NotNull @Positive Long userId,
                                                    @PathVariable @NotNull @Positive Long requestId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.cancelRequest(userId, requestId));
    }
}
