package ru.practicum.explorewithme.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserShortDto {
    private Long id;
    private String name;
}