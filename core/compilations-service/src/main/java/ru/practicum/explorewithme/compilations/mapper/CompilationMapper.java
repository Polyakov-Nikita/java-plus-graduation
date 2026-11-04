package ru.practicum.explorewithme.compilations.mapper;

import ru.practicum.explorewithme.compilations.dto.CompilationDto;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.entity.Compilation;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto body) {
        return Compilation.builder()
                .pinned(body.isPinned())
                .title(body.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }
}
