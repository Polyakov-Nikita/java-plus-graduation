package ru.practicum.explorewithme.analyzer.entity;

import java.io.Serializable;

public record EventsSimilarityId(Long eventA, Long eventB) implements Serializable {
}
