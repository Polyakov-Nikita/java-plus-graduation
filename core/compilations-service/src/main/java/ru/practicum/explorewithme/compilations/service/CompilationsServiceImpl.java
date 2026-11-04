package ru.practicum.explorewithme.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.EventDto;
import ru.practicum.explorewithme.common.service.EventService;
import ru.practicum.explorewithme.compilations.dto.CompilationDto;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.compilations.entity.Compilation;
import ru.practicum.explorewithme.compilations.entity.CompilationEvent;
import ru.practicum.explorewithme.compilations.exception.CompilationNotFoundException;
import ru.practicum.explorewithme.compilations.mapper.CompilationMapper;
import ru.practicum.explorewithme.compilations.repository.CompilationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto body) {
        log.trace("Инициировано сохранение подборки. Тело запроса: {}", body);
        List<EventDto> events = getEvents(body.getEvents());
        log.trace("Найдено {} событий", events.size());
        Compilation compilation = CompilationMapper.toCompilation(body);
        linkEvents(compilation, events);
        Compilation result = compilationRepository.save(compilation);
        log.debug("Подборка {} сохранена", compilation);
        return buildDto(result, events);
    }

    private List<EventDto> getEvents(List<Long> eventIds) {
        if (eventIds == null) {
            return List.of();
        }
        return eventService.getEvents(eventIds);
    }

    private void linkEvents(Compilation compilation, List<EventDto> events) {
        List<CompilationEvent> compilationEvents = compilation.getEvents();
        compilationEvents.clear();
        events.forEach(event -> {
                    CompilationEvent compilationEvent = CompilationEvent.builder()
                            .compilation(compilation)
                            .eventId(event.getId())
                            .build();
                    compilationEvents.add(compilationEvent);
                }
        );
    }

    private CompilationDto buildDto(Compilation compilation, List<EventDto> events) {
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        List<Long> eventIds = compilation.getEvents().stream()
                .map(CompilationEvent::getEventId)
                .toList();
        if (eventIds.isEmpty()) {
            return compilationDto;
        }
        compilationDto.setEvents(events);
        return compilationDto;
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        log.trace("Инициировано удаление подборки с id={}", compId);
        checkCompilationExistence(compId);
        compilationRepository.deleteById(compId);
        log.debug("Подборка с id={} удалена", compId);
    }

    private void checkCompilationExistence(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException(compId);
        }
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest body) {
        log.trace("Инициировано обновление сборки с id={}. Тело запроса: {}", compId, body);
        Compilation compilation = findCompilation(compId);
        log.trace("Подборка найдена: {}", compilation);
        List<EventDto> events;
        List<Long> eventIdsUpdate = body.getEvents();
        if (eventIdsUpdate == null) {
            events = getEventsOf(compilation);
        } else {
            events = getEvents(eventIdsUpdate);
            linkEvents(compilation, events);
        }
        Compilation update = updateCompilationData(compilation, body);
        log.debug("Создано обновление подборки: {}", update);
        Compilation result = compilationRepository.save(update);
        log.debug("Обновление {} сохранено", update);
        return buildDto(result, events);
    }

    private Compilation findCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));
    }

    private List<EventDto> getEventsOf(Compilation compilation) {
        return getEvents(
                compilation.getEvents().stream()
                        .map(CompilationEvent::getEventId)
                        .toList()
        );
    }

    private Compilation updateCompilationData(Compilation compilation, UpdateCompilationRequest update) {
        Boolean pinnedUpdate = update.getPinned();
        if (pinnedUpdate != null) {
            compilation.setPinned(pinnedUpdate);
        }
        String titleUpdate = update.getTitle();
        if (titleUpdate != null) {
            compilation.setTitle(titleUpdate);
        }
        return compilation;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.trace("Инициировано получение подборок с параметрами pinned={}, from={} и size={}", pinned, from, size);
        List<Compilation> result = compilationRepository.findWithOffset(pinned, from, size);
        log.debug("Найдено {} подборок", result.size());
        Map<Compilation, List<EventDto>> compilationsEvents = getCompilationsEvents(result);
        return result.stream()
                .map(compilation -> buildDto(compilation, compilationsEvents.get(compilation)))
                .toList();
    }

    private Map<Compilation, List<EventDto>> getCompilationsEvents(List<Compilation> compilations) {
        Map<Compilation, List<EventDto>> result = new HashMap<>();
        compilations.forEach(
                compilation -> {
                    List<EventDto> events = getEventsOf(compilation);
                    result.put(compilation, events);
                }
        );
        return result;
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        log.trace("Инициировано получение подборки с id={}", compId);
        Compilation result = findCompilation(compId);
        List<EventDto> events = getEventsOf(result);
        log.debug("Найдена подборка {}", result);
        return buildDto(result, events);
    }
}
