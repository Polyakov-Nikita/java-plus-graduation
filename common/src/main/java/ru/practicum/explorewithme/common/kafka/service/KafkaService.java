package ru.practicum.explorewithme.common.kafka.service;

import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaService {
    void handle(SpecificRecordBase record);
}
