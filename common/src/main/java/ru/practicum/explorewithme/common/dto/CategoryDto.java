package ru.practicum.explorewithme.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CategoryDto {
    private long id;
    private String name;
}
