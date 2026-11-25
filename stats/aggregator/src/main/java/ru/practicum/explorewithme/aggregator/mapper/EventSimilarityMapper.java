package ru.practicum.explorewithme.aggregator.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.explorewithme.common.kafka.producer.RecordPayload;

@Component
public class EventSimilarityMapper {
    public RecordPayload toRecordPayload(EventSimilarityAvro eventSimilarityAvro) {
        return RecordPayload.builder()
                .timestamp(eventSimilarityAvro.getTimestamp())
                .key(eventSimilarityAvro.getEventA())
                .value(eventSimilarityAvro)
                .build();
    }
}
