package ru.practicum.explorewithme.compilations.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import ru.practicum.test.utils.ServiceTest;

import java.util.List;

public class CompilationsServiceImplTest extends ServiceTest {
    @InjectMocks
    private CompilationsServiceImpl compilationService;
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private EventService eventService;

    @Test
    public void createCompilation_ReturnsObject() {
        // Arrange
        List<Long> eventIds = List.of(1L);
        NewCompilationDto body = NewCompilationDto.builder()
                .events(eventIds)
                .pinned(false)
                .title("Compilation Title")
                .build();
        long savedId = 1L;
        Compilation saved = buildCompilation(savedId, body);
        List<EventDto> events = createEvents(body.getEvents());
        whenEventsFound(events);
        whenSaveReturns(compilationRepository, saved);

        // Act
        CompilationDto actual = compilationService.createCompilation(body);

        // Assert
        CompilationDto expected = buildCompilationDto(saved);
        assertEquals(actual, expected);
    }

    @Test
    public void createCompilation_WithoutEvents_ReturnsObject() {
        // Arrange
        NewCompilationDto body = NewCompilationDto.builder()
                .pinned(false)
                .title("Compilation Title")
                .build();
        long savedId = 1L;
        Compilation saved = buildCompilation(savedId, body);
        whenSaveReturns(compilationRepository, saved);

        // Act
        CompilationDto actual = compilationService.createCompilation(body);

        // Assert
        CompilationDto expected = buildCompilationDto(saved);
        assertEquals(actual, expected);
    }

    @Test
    public void deleteCompilation() {
        // Arrange
        long id = 1L;
        whenEntityExistIn(compilationRepository);
        doNothingOnDeleteIn(compilationRepository);

        // Act
        compilationService.deleteCompilation(id);

        // Assert
        assertMethodCall(compilationRepository, repository -> repository.deleteById(Mockito.eq(id)));
    }

    @Test
    public void deleteCompilation_AbsentCompilation_NotFoundException() {
        // Arrange
        long absentId = 1L;
        whenEntityAbsentIn(compilationRepository);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> compilationService.deleteCompilation(absentId));

        // Assert
        assertException(thrown, CompilationNotFoundException.class);
    }

    @Test
    public void updateCompilation_ReturnsObject() {
        // Arrange
        List<Long> eventIds = List.of(1L);
        UpdateCompilationRequest body = UpdateCompilationRequest.builder()
                .events(eventIds)
                .pinned(true)
                .title("Compilation Title Update")
                .build();
        long savedId = 1L;
        Compilation saved = buildCompilation(savedId);
        List<EventDto> events = createEvents(body.getEvents());
        Compilation update = buildCompilation(saved, body);
        whenEntityFoundIn(compilationRepository, saved);
        whenEventsFound(events);
        whenSaveReturns(compilationRepository, update);

        // Act
        CompilationDto actual = compilationService.updateCompilation(savedId, body);

        // Assert
        CompilationDto expected = buildCompilationDto(update);
        assertEquals(actual, expected);
    }

    @Test
    public void updateCompilation_EventsOnly_ReturnsObject() {
        // Arrange
        List<Long> eventIds = List.of(1L);
        UpdateCompilationRequest body = buildEventsUpdateCompilationRequest(eventIds);
        long savedId = 1L;
        Compilation saved = buildCompilation(savedId);
        List<EventDto> events = createEvents(body.getEvents());
        Compilation update = buildCompilation(saved, body);
        whenEntityFoundIn(compilationRepository, saved);
        whenEventsFound(events);
        whenSaveReturns(compilationRepository, update);

        // Act
        CompilationDto actual = compilationService.updateCompilation(savedId, body);

        // Assert
        CompilationDto expected = buildCompilationDto(update);
        assertEquals(actual, expected);
    }

    @Test
    public void updateCompilation_PinnedOnly_ReturnsObject() {
        // Arrange
        Boolean pinned = true;
        UpdateCompilationRequest body = buildPinnedUpdateCompilationRequest(pinned);
        long savedId = 1L;
        Compilation saved = buildCompilation(savedId);
        Compilation update = buildCompilation(saved, body);
        whenEntityFoundIn(compilationRepository, saved);
        whenSaveReturns(compilationRepository, update);

        // Act
        CompilationDto actual = compilationService.updateCompilation(savedId, body);

        // Assert
        CompilationDto expected = buildCompilationDto(update);
        assertEquals(actual, expected);
    }

    @Test
    public void updateCompilation_TitleOnly_ReturnsObject() {
        // Arrange
        String title = "Compilation Title Update";
        UpdateCompilationRequest body = buildTitleUpdateCompilationRequest(title);
        long savedId = 1L;
        Compilation saved = buildCompilation(savedId);
        Compilation update = buildCompilation(saved, body);
        whenEntityFoundIn(compilationRepository, saved);
        whenSaveReturns(compilationRepository, update);

        // Act
        CompilationDto actual = compilationService.updateCompilation(savedId, body);

        // Assert
        CompilationDto expected = buildCompilationDto(update);
        assertEquals(actual, expected);
    }

    @Test
    public void updateCompilation_AbsentCompilation_ReturnsObject() {
        // Arrange
        List<Long> eventIds = List.of(1L);
        UpdateCompilationRequest body = UpdateCompilationRequest.builder()
                .events(eventIds)
                .pinned(true)
                .title("Compilation Title Update")
                .build();
        long absentId = 1L;
        whenEntityNotFoundIn(compilationRepository);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> compilationService.updateCompilation(absentId, body));

        // Assert
        assertException(thrown, CompilationNotFoundException.class);
    }

    @Test
    public void getCompilations_ReturnsArray() {
        // Arrange
        Boolean pinned = false;
        int from = 0;
        int size = 10;
        List<Compilation> compilations = List.of(
                buildCompilation(1),
                buildCompilation(2)
        );
        whenFindWithOffsetReturns(compilations);

        // Act
        List<CompilationDto> actual = compilationService.getCompilations(pinned, from, size);

        // Assert
        List<CompilationDto> expected = compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
        assertEquals(actual, expected);
    }

    @Test
    public void getCompilation_ReturnsObject() {
        // Arrange
        long compilationId = 1L;
        Compilation compilation = buildCompilation(compilationId);
        whenEntityFoundIn(compilationRepository, compilation);

        // Act
        CompilationDto actual = compilationService.getCompilation(compilationId);

        // Assert
        CompilationDto expected = buildCompilationDto(compilation);
        assertEquals(actual, expected);
    }

    @Test
    public void getCompilation_AbsentCompilation_NotFoundException() {
        // Arrange
        long absentId = 1L;
        whenEntityNotFoundIn(compilationRepository);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> compilationService.getCompilation(absentId));

        // Assert
        assertException(thrown, CompilationNotFoundException.class);
    }

    private List<EventDto> createEvents(List<Long> eventIds) {
        return eventIds.stream()
                .map(this::buildEvent)
                .toList();
    }

    protected void whenEventsFound(List<EventDto> events) {
        Mockito.when(eventService.getEvents(Mockito.any()))
                .thenReturn(events);
    }

    private void whenFindWithOffsetReturns(List<Compilation> compilations) {
        Mockito.when(compilationRepository.findWithOffset(Mockito.any(Boolean.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(compilations);
    }

    private CompilationDto buildCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .events(
                        compilation.getEvents().stream()
                                .map(compilationEvent ->
                                        EventDto.builder()
                                                .id(compilationEvent.getEventId())
                                                .build()
                                )
                                .toList()
                )
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    private UpdateCompilationRequest buildEventsUpdateCompilationRequest(List<Long> eventIds) {
        return UpdateCompilationRequest.builder()
                .events(eventIds)
                .build();
    }

    private UpdateCompilationRequest buildPinnedUpdateCompilationRequest(Boolean pinned) {
        return UpdateCompilationRequest.builder()
                .pinned(pinned)
                .build();
    }

    private UpdateCompilationRequest buildTitleUpdateCompilationRequest(String title) {
        return UpdateCompilationRequest.builder()
                .title(title)
                .build();
    }

    private Compilation buildCompilation(long id) {
        return Compilation.builder()
                .id(id)
                .pinned(false)
                .title("Compilation Title")
                .build();
    }


    private Compilation buildCompilation(long id, NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        return Compilation.builder()
                .id(id)
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .events(eventIds != null ?
                        eventIds.stream()
                                .map(eventId -> CompilationEvent.builder()
                                        .eventId(eventId)
                                        .build())
                                .toList()
                        : List.of()
                )
                .build();
    }


    private Compilation buildCompilation(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        Boolean pinned = updateCompilationRequest.getPinned();
        String title = updateCompilationRequest.getTitle();
        List<Long> eventIds = updateCompilationRequest.getEvents();
        return Compilation.builder()
                .id(compilation.getId())
                .pinned(pinned != null ? pinned : compilation.isPinned())
                .title(title != null ? title : compilation.getTitle())
                .events(eventIds != null ?
                        eventIds.stream()
                                .map(eventId -> CompilationEvent.builder()
                                        .eventId(eventId)
                                        .build())
                                .toList()
                        : compilation.getEvents()
                )
                .build();
    }

    private EventDto buildEvent(long id) {
        return EventDto.builder()
                .id(id)
                .build();
    }
}
