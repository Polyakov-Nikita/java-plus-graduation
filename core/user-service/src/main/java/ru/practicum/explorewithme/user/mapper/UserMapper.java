package ru.practicum.explorewithme.user.mapper;

import ru.practicum.explorewithme.common.dto.NewUserRequest;
import ru.practicum.explorewithme.common.dto.UserDto;
import ru.practicum.explorewithme.user.entity.User;
import ru.practicum.explorewithme.common.dto.UserShortDto;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toUser(NewUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
