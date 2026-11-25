package ru.practicum.explorewithme.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.explorewithme.aggregator.mapper.EventSimilarityMapper;
import ru.practicum.explorewithme.common.config.ScoreProperties;
import ru.practicum.explorewithme.common.kafka.producer.RecordPayload;
import ru.practicum.explorewithme.common.kafka.producer.StatsProducer;
import ru.practicum.explorewithme.common.kafka.service.KafkaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(ScoreProperties.class)
public class AggregatorService implements KafkaService {
    private final ScoreProperties scoreProperties;
    private final Map<Long, Map<Long, Double>> weightMatrix = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, Double>> minSumMap = new ConcurrentHashMap<>();
    private final Map<Long, Double> eventSumMap = new ConcurrentHashMap<>();
    private final StatsProducer producer;
    private final EventSimilarityMapper eventSimilarityMapper;

    @Override
    public void handle(SpecificRecordBase record) {
        UserActionAvro userAction = (UserActionAvro) record;
        List<EventSimilarityAvro> similarities = computeSimilarity(userAction);
        List<RecordPayload> records = similarities.stream()
                .map(eventSimilarityMapper::toRecordPayload)
                .toList();
        producer.send(records);
    }

    private List<EventSimilarityAvro> computeSimilarity(UserActionAvro action) {
        double score = scoreProperties.getScore(action.getActionType());
        Long actionEventId = action.getEventId();
        Long actionUserId = action.getUserId();
        Map<Long, Double> eventVector = weightMatrix.get(actionEventId);
        Double oldWeight = eventVector == null ? null : eventVector.get(actionUserId);
        List<Long> affectedEventIds = new ArrayList<>();
        if (oldWeight == null || score > oldWeight) {
            double weightDelta = oldWeight == null ? score : score - oldWeight;
            for (Map.Entry<Long, Map<Long, Double>> matrixEntry : weightMatrix.entrySet()) {
                Long otherEventId = matrixEntry.getKey();
                if (otherEventId.equals(actionEventId)) {
                    continue;
                }
                Double otherWeight = matrixEntry.getValue().get(actionUserId);
                if (otherWeight == null) {
                    continue;
                }
                double oldMin = oldWeight == null ? 0.0 : Math.min(oldWeight, otherWeight);
                double newMin = Math.min(score, otherWeight);
                if (newMin > oldMin) {
                    addMinSum(actionEventId, otherEventId, newMin - oldMin);
                }
                affectedEventIds.add(otherEventId);
            }
            eventSumMap.merge(actionEventId, weightDelta, Double::sum);
            weightMatrix.computeIfAbsent(actionEventId, event -> new ConcurrentHashMap<>())
                    .put(actionUserId, score);
        }
        return affectedEventIds.stream()
                .map(
                        eventId -> {
                            long eventA = Math.min(actionEventId, eventId);
                            long eventB = Math.max(actionEventId, eventId);
                            return EventSimilarityAvro.newBuilder()
                                    .setEventA(eventA)
                                    .setEventB(eventB)
                                    .setScore(computeScoreBetween(actionEventId, eventId))
                                    .setTimestamp(action.getTimestamp())
                                    .build();
                        }
                )
                .toList();
    }

    private void addMinSum(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        minSumMap.computeIfAbsent(first, e -> new ConcurrentHashMap<>())
                .merge(second, sum, Double::sum);
    }

    private double computeScoreBetween(Long eventAId, Long eventBId) {
        double sumMin = getMinSum(eventAId, eventBId);
        double sumA = eventSumMap.get(eventAId);
        double sumB = eventSumMap.get(eventBId);
        if (sumA == 0.0 || sumB == 0.0) {
            return 0.0;
        }
        return sumMin / (Math.sqrt(sumA) * Math.sqrt(sumB));
    }

    private double getMinSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        return minSumMap.getOrDefault(first, Map.of()).getOrDefault(second, 0.0);
    }
}
