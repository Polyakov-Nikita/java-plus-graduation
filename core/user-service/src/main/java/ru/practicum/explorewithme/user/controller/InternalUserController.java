package ru.practicum.explorewithme.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.user.dto.UserShortDto;
import ru.practicum.explorewithme.user.service.UserService;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = ACCESS_INTERNAL + URL_USERS)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class InternalUserController {
    private final UserService userService;

    @GetMapping(URL_EXISTS + "/{" + ID_USER + "}")
    public ResponseEntity<Boolean> existsById(@PathVariable(name = ID_USER) long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.existsByIdInternal(userId));
    }

    @GetMapping("/{" + ID_USER + "}")
    public ResponseEntity<UserShortDto> getUserById(@PathVariable(name = ID_USER) long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserInternal(userId));
    }
}
