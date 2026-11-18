package ru.practicum.explorewithme.request.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import ru.practicum.explorewithme.request.feign.dto.RequestEventDto;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "event-service")
public interface RequestEventClient {
    @GetMapping(ACCESS_INTERNAL + URL_EVENTS + "/{" + ID_EVENT + "}")
    RequestEventDto getEventById(@PathVariable(name = ID_EVENT) long eventId);

    @PutMapping(ACCESS_INTERNAL + URL_EVENTS + "/{" + ID_EVENT + "}" + URL_REQUESTS + URL_INCREMENT)
    void incrementConfirmedRequests(@PathVariable(name = ID_EVENT) long eventId);

    @PutMapping(ACCESS_INTERNAL + URL_EVENTS + "/{" + ID_EVENT + "}" + URL_REQUESTS + URL_DECREMENT)
    void decrementConfirmedRequests(@PathVariable(name = ID_EVENT) long eventId);
}
