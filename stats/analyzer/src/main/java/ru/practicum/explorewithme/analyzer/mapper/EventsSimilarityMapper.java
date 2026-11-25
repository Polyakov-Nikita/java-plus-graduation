package ru.practicum.explorewithme.analyzer.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.explorewithme.analyzer.entity.EventsSimilarity;

@Component
@RequiredArgsConstructor
public class EventsSimilarityMapper {
    public EventsSimilarity toEventsSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        return EventsSimilarity.builder()
                .eventA(eventSimilarityAvro.getEventA())
                .eventB(eventSimilarityAvro.getEventB())
                .similarity(eventSimilarityAvro.getScore())
                .actionTime(eventSimilarityAvro.getTimestamp())
                .build();
    }
}
