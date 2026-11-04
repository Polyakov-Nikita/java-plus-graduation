package ru.practicum.explorewithme.compilations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.explorewithme.common.dto.EventDto;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
public class CompilationDto {
    @Setter
    @Builder.Default
    private List<EventDto> events = new ArrayList<>();
    private long id;
    private boolean pinned;
    private String title;
}
