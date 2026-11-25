package ru.practicum.explorewithme.common.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@Slf4j
public class StatsProducer implements AutoCloseable {
    private final KafkaProducer<Long, SpecificRecordBase> producer;
    private final Duration closeDuration;
    private final String topic;

    public StatsProducer(ProducerProperties producerProperties) {
        this.producer = new KafkaProducer<>(producerProperties.getProperties());
        this.closeDuration = producerProperties.getCloseDuration();
        this.topic = producerProperties.getTopic();
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(closeDuration);
    }

    public void send(RecordPayload record) {
        ProducerRecord<Long, SpecificRecordBase> producerRecord = createRecord(record);
        Future<RecordMetadata> futureResult = producer.send(producerRecord);
        flush(futureResult);
    }

    private ProducerRecord<Long, SpecificRecordBase> createRecord(RecordPayload record) {
        return new ProducerRecord<>(
                topic,
                null,
                record.timestamp().toEpochMilli(),
                record.key(),
                record.value()
        );
    }

    private void flush(Future<RecordMetadata> futureResult) {
        producer.flush();
        checkMetadata(futureResult);
    }

    private void checkMetadata(Future<RecordMetadata> futureResult) {
        try {
            RecordMetadata metadata = futureResult.get();
            log.info("record was successfully saved: topic '{}', partition '{}', offset '{}' ",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("couldn't record in topic '{}'", topic, e);
        }
    }

    public void send(List<RecordPayload> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Future<RecordMetadata>> futures = new ArrayList<>(records.size());
        for (RecordPayload record : records) {
            ProducerRecord<Long, SpecificRecordBase> producerRecord = createRecord(record);
            futures.add(producer.send(producerRecord));
        }
        flush(futures);
    }

    private void flush(List<Future<RecordMetadata>> futures) {
        producer.flush();
        futures.forEach(this::checkMetadata);
    }
}
