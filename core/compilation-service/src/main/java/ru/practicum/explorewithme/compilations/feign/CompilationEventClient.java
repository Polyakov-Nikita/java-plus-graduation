package ru.practicum.explorewithme.compilations.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.compilations.feign.dto.CompilationEventDto;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "event-service")
public interface CompilationEventClient {
    @GetMapping(ACCESS_INTERNAL + URL_EVENTS)
    List<CompilationEventDto> getEventsByIds(@RequestParam(name = PARAM_IDS) List<Long> ids);
}
