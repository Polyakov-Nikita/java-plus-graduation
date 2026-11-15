package ru.practicum.explorewithme.event.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.event.feign.dto.EventUserDto;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "user-service")
public interface EventUserClient {
    @GetMapping(ACCESS_INTERNAL + URL_USERS + "/{" + ID_USER + "}")
    EventUserDto getUserById(@PathVariable(name = ID_USER) long userId);
}
