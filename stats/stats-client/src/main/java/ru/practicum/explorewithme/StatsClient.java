package ru.practicum.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explorewithme.exception.StatsServerUnavailableException;
import ru.practicum.explorewithme.hit.EndpointHitRequest;
import ru.practicum.explorewithme.stats.ViewStatsResponse;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsServiceId;
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private static final String API_PREFIX_HIT = "/hit";
    private static final String API_PREFIX_STATS = "/stats";
    private static final String PARAM_START = "start";
    private static final String PARAM_END = "end";
    private static final String PARAM_URIS = "uris";
    private static final String PARAM_UNIQUE = "unique";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String statsServiceId, RestTemplateBuilder builder, DiscoveryClient discoveryClient, RetryTemplate retryTemplate) {
        this.statsServiceId = statsServiceId;
        this.restTemplate = builder.build();
        this.discoveryClient = discoveryClient;
        this.retryTemplate = retryTemplate;
    }

    public void addStatistics(EndpointHitRequest endpointHitRequest) {
        log.trace("Отправлен запрос на добавление статистических данных {}", endpointHitRequest);
        restTemplate.postForEntity(makeUri() + API_PREFIX_HIT, endpointHitRequest, Void.class);
    }

    public List<ViewStatsResponse> getStatistics(LocalDateTime start,
                                                 LocalDateTime end,
                                                 List<String> uris,
                                                 boolean unique) {
        log.trace("Отправлен запрос на получение статистики: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(makeUri())
                .path(API_PREFIX_STATS)
                .queryParam(PARAM_START, DATE_TIME_FORMATTER.format(start))
                .queryParam(PARAM_END, DATE_TIME_FORMATTER.format(end));

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> builder.queryParam(PARAM_URIS, uri));
        }

        builder.queryParam(PARAM_UNIQUE, String.valueOf(unique));

        URI url = builder.build().encode().toUri();

        ResponseEntity<List<ViewStatsResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        List<ViewStatsResponse> body = response.getBody();
        return body != null ? body : Collections.emptyList();
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailableException(statsServiceId, exception);
        }
    }

    private String makeUri() {
        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort()).toString();
    }
}
