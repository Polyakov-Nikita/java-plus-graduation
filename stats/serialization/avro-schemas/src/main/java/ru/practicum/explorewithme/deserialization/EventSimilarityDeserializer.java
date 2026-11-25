package ru.practicum.explorewithme.deserialization;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@SuppressWarnings("unused")
public class EventSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public EventSimilarityDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}
