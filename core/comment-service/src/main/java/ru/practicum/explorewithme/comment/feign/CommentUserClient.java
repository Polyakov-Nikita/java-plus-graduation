package ru.practicum.explorewithme.comment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.comment.feign.dto.CommentUserDto;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@FeignClient(name = "user-service")
public interface CommentUserClient {
    @GetMapping(ACCESS_INTERNAL + URL_USERS + URL_EXISTS + "/{" + ID_USER + "}")
    Boolean existsById(@PathVariable(name = ID_USER) long userId);

    @GetMapping(ACCESS_INTERNAL + URL_USERS + "/{" + ID_USER + "}")
    CommentUserDto getUserById(@PathVariable(name = ID_USER) long userId);
}
