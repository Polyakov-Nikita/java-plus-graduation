package ru.practicum.explorewithme.compilations.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.compilations.service.CompilationsService;
import ru.practicum.test.utils.ControllerTest;

import java.util.List;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.ACCESS_ADMIN;
import static ru.practicum.explorewithme.common.controller.ControllerConstants.URL_COMPILATIONS;

public class AdminCompilationsControllerTest extends ControllerTest {
    private static final String URL_BASE = ACCESS_ADMIN + URL_COMPILATIONS;
    @InjectMocks
    private AdminCompilationsController adminCompilationsController;
    @Mock
    private CompilationsService compilationsService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminCompilationsController)
                .build();
    }

    @Test
    public void createCompilation_StatusCreated() {
        // Arrange
        NewCompilationDto body = NewCompilationDto.builder()
                .events(List.of())
                .pinned(false)
                .title("Compilation Title")
                .build();

        // Act
        ResultActions result = performPost(URL_BASE, body);

        // Assert
        expectStatusCreated(result);
        assertMethodCall(compilationsService, service ->
                service.createCompilation(Mockito.any(NewCompilationDto.class)));
    }

    @Test
    public void deleteCompilation_StatusNoContent() {
        // Arrange
        long compId = 1L;

        // Act
        ResultActions result = performDelete(createIdUrl(URL_BASE, compId));

        // Assert
        expectStatusNoContent(result);
        assertMethodCall(compilationsService, service ->
                service.deleteCompilation(Mockito.eq(compId)));
    }

    @Test
    public void updateCompilation_StatusOk() {
        // Arrange
        long compId = 1L;
        UpdateCompilationRequest body = UpdateCompilationRequest.builder()
                .events(List.of())
                .pinned(true)
                .title("Compilation Title Update")
                .build();

        // Act
        ResultActions result = performPatch(createIdUrl(URL_BASE, compId), body);

        // Assert
        expectStatusOk(result);
        assertMethodCall(compilationsService, service ->
                service.updateCompilation(Mockito.eq(compId), Mockito.any(UpdateCompilationRequest.class)));
    }
}
