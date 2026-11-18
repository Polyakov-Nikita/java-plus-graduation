package ru.practicum.explorewithme.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewCommentDto {
    @NotBlank
    private String text;
}
