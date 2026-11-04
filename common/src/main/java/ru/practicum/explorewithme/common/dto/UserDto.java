package ru.practicum.explorewithme.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
