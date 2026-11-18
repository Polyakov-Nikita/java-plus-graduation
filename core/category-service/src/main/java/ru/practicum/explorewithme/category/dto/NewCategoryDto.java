package ru.practicum.explorewithme.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewCategoryDto {
    @NotBlank(message = "'name' field cannot be empty.")
    @Size(max = 50, message = "too long name string")
    private String name;
}
