package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.explorewithme.entity.EndpointHit;
import ru.practicum.explorewithme.hit.EndpointHitRequest;
import ru.practicum.explorewithme.repository.EndpointHitRepository;
import ru.practicum.explorewithme.service.mapper.EndpointHitMapper;
import ru.practicum.test.utils.ServiceTest;

import java.time.LocalDateTime;

public class EndpointHitServiceImplTest extends ServiceTest {
    private final EndpointHitMapper endpointHitMapper = new EndpointHitMapper();

    private EndpointHitServiceImpl hitService;
    @Mock
    private EndpointHitRepository endpointHitRepository;

    @BeforeEach
    public void setUp() {
        hitService = new EndpointHitServiceImpl(endpointHitMapper, endpointHitRepository);
    }

    @Test
    public void saveHit_saveCalls() {
        // Arrange
        EndpointHitRequest request = EndpointHitRequest.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.now().plusDays(1))
                .build();

        // Act
        hitService.saveHit(request);

        // Assert
        assertMethodCall(endpointHitRepository, repository ->
                repository.save(Mockito.any(EndpointHit.class)));
    }
}
