package ru.practicum.explorewithme.compilations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.explorewithme.compilations.feign.dto.CompilationEventDto;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
public class CompilationDto {
    @Setter
    @Builder.Default
    private List<CompilationEventDto> events = new ArrayList<>();
    private long id;
    private boolean pinned;
    private String title;
}
