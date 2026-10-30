package ru.practicum.explorewithme.exception;

import lombok.Getter;

@Getter
public class StatsServerUnavailableException extends RuntimeException {
    private final String statsServiceId;

    public StatsServerUnavailableException(String statsServiceId, Exception exception) {
        super("Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId, exception);
        this.statsServiceId = statsServiceId;
    }
}
