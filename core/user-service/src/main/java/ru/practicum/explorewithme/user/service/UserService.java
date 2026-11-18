package ru.practicum.explorewithme.user.service;

import ru.practicum.explorewithme.user.dto.NewUserRequest;
import ru.practicum.explorewithme.user.dto.UserDto;
import ru.practicum.explorewithme.user.dto.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest request);

    void deleteUser(Long userId);

    UserShortDto getUserInternal(long userId);

    Boolean existsByIdInternal(long id);
}
