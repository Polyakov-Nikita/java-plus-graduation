package ru.practicum.explorewithme.common.kafka.starter;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import ru.practicum.explorewithme.common.kafka.consumer.ConsumerProperties;
import ru.practicum.explorewithme.common.kafka.service.KafkaService;

import java.time.Duration;
import java.util.List;

@Slf4j
public class ServiceStarter {
    private final KafkaConsumer<Long, SpecificRecordBase> consumer;
    private final KafkaService kafkaService;
    private final List<String> topics;
    private final Duration attemptTimeout;

    public ServiceStarter(ConsumerProperties consumerProperties, KafkaService kafkaService) {
        this.consumer = new KafkaConsumer<>(consumerProperties.getProperties());
        this.kafkaService = kafkaService;
        this.topics = consumerProperties.getTopics();
        this.attemptTimeout = consumerProperties.getAttemptTimeout();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(attemptTimeout);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                        kafkaService.handle(record.value());
                    }
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("wakeup exception");
        } catch (Exception e) {
            log.error("starter error", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
