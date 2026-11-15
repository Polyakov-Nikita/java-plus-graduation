package ru.practicum.explorewithme.comment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.comment.feign.dto.CommentEventDto;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "event-service")
public interface CommentEventClient {
    @GetMapping(ACCESS_INTERNAL + URL_EVENTS + "/{" + ID_EVENT + "}")
    CommentEventDto getEventById(@PathVariable(name = ID_EVENT) long eventId);
}
