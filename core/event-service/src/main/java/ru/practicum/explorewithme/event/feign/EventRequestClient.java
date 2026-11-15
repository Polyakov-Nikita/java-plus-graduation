package ru.practicum.explorewithme.event.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.common.dto.RequestStatus;
import ru.practicum.explorewithme.event.feign.dto.EventRequestDto;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "request-service")
public interface EventRequestClient {
    @GetMapping(ACCESS_INTERNAL + URL_REQUESTS + "/{" + ID_EVENT + "}")
    List<EventRequestDto> getRequests(@PathVariable(name = ID_EVENT) long eventId);

    @PutMapping(ACCESS_INTERNAL + URL_REQUESTS)
    List<EventRequestDto> changeRequestStatuses(
            @RequestParam(name = PARAM_IDS) List<Long> ids,
            @RequestParam(name = PARAM_STATUS) RequestStatus status
    );
}
