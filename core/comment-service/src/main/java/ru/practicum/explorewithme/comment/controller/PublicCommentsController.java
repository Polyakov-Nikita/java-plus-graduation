package ru.practicum.explorewithme.comment.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.comment.dto.CommentShortDto;
import ru.practicum.explorewithme.comment.service.CommentService;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = URL_EVENTS + "/{" + ID_EVENT + "}" + URL_COMMENTS)
@RequiredArgsConstructor
@Validated
@SuppressWarnings("unused")
public class PublicCommentsController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentShortDto>> getComments(
            @PathVariable @NotNull @Positive Long eventId,
            @RequestParam(name = PARAM_FROM, required = false, defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(name = PARAM_SIZE, required = false, defaultValue = "10")
            @Positive int size
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getComments(eventId, from, size));
    }
}
