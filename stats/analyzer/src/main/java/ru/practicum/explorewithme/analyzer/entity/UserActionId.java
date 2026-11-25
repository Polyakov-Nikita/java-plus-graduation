package ru.practicum.explorewithme.analyzer.entity;

import java.io.Serializable;

public record UserActionId(Long userId, Long eventId) implements Serializable {
}
