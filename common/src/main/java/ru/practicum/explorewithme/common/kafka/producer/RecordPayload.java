package ru.practicum.explorewithme.common.kafka.producer;

import lombok.Builder;
import org.apache.avro.specific.SpecificRecordBase;

import java.time.Instant;

@Builder
public record RecordPayload(
        Instant timestamp,
        Long key,
        SpecificRecordBase value
) {
}
