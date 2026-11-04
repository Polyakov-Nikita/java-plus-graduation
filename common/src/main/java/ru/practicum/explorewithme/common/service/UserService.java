package ru.practicum.explorewithme.common.service;

import ru.practicum.explorewithme.common.dto.NewUserRequest;
import ru.practicum.explorewithme.common.dto.UserDto;
import ru.practicum.explorewithme.common.dto.UserShortDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserShortDto getUser(Long userId);

    UserDto createUser(NewUserRequest request);

    void deleteUser(Long userId);

    Boolean existsById(long id);

    Map<Long, UserShortDto> getUsersMap(List<Long> ids);
}
