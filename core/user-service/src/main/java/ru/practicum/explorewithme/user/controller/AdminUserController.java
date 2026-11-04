package ru.practicum.explorewithme.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.controller.ControllerConstants;
import ru.practicum.explorewithme.common.dto.NewUserRequest;
import ru.practicum.explorewithme.common.dto.UserDto;
import ru.practicum.explorewithme.common.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = ControllerConstants.ACCESS_ADMIN + ControllerConstants.URL_USERS)
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(name = ControllerConstants.PARAM_FROM, defaultValue = "0") int from,
            @RequestParam(name = ControllerConstants.PARAM_SIZE, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getUsers(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid NewUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @DeleteMapping("/{" + ControllerConstants.ID_USER + "}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = ControllerConstants.ID_USER) Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
