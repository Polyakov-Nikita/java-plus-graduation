package ru.practicum.explorewithme.comment.feign.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentUserDto {
    private Long id;
    private String name;
}