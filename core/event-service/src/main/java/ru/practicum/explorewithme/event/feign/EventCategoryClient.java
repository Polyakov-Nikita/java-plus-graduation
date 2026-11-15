package ru.practicum.explorewithme.event.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.event.feign.dto.EventCategoryDto;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "category-service")
public interface EventCategoryClient {
    @GetMapping(ACCESS_INTERNAL + URL_CATEGORIES + "/{" + ID_CATEGORY + "}")
    EventCategoryDto getCategoryById(@PathVariable(name = ID_CATEGORY) long catId);
}
