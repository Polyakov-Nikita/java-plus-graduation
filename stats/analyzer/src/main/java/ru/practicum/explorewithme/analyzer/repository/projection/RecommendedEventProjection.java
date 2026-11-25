package ru.practicum.explorewithme.analyzer.repository.projection;

public interface RecommendedEventProjection {
    long getEventId();
    double getSimilarity();
}
