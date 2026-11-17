package ru.practicum.explorewithme.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class NewCompilationDto {
    private List<Long> events;
    private boolean pinned;
    @NotBlank(message = "'title' field cannot be empty.")
    @Size(min = 1, max = 50, message = "incorrect title string length")
    private String title;
}
