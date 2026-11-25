package ru.practicum.explorewithme.analyzer.repository.projection;

public interface InteractionCountProjection {
    long getEventId();
    double getScore();
}
