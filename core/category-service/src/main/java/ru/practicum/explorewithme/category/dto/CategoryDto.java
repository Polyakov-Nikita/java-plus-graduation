package ru.practicum.explorewithme.category.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryDto {
    private long id;
    private String name;
}
