package ru.practicum.explorewithme.category.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "event-service")
public interface CategoryEventClient {
    @GetMapping(ACCESS_INTERNAL + URL_EVENTS + URL_EXISTS + URL_CATEGORIES + "/{" + ID_CATEGORY + "}" )
    Boolean existsByCategoryId(@PathVariable(name = ID_CATEGORY) long catId);
}
