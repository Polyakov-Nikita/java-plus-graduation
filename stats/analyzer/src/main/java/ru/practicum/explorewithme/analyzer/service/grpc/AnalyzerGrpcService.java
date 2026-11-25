package ru.practicum.explorewithme.analyzer.service.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.message.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.message.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.message.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.message.UserPredictionsRequestProto;
import ru.practicum.explorewithme.analyzer.repository.EventsSimilarityRepository;
import ru.practicum.explorewithme.analyzer.repository.UserActionRepository;
import ru.practicum.explorewithme.analyzer.repository.projection.InteractionCountProjection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyzerGrpcService {
    private final UserActionRepository userActionRepository;
    private final EventsSimilarityRepository eventsSimilarityRepository;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        return eventsSimilarityRepository.findRecommendationsForUser(
                        request.getUserId(),
                        request.getMaxResults()
                ).stream()
                .map(
                        row -> RecommendedEventProto.newBuilder()
                                .setEventId(row.getEventId())
                                .setScore(row.getSimilarity())
                                .build()
                )
                .toList();
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        return eventsSimilarityRepository.findSimilarEvents(
                        request.getEventId(),
                        request.getUserId(),
                        request.getMaxResults()
                ).stream()
                .map(
                        row -> RecommendedEventProto.newBuilder()
                                .setEventId(row.getEventId())
                                .setScore(row.getSimilarity())
                                .build()
                )
                .toList();
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        List<Long> eventIds = request.getEventIdList();
        if (eventIds.isEmpty()) {
            return List.of();
        }
        Map<Long, Double> counts = userActionRepository.countInteractions(eventIds).stream()
                .collect
                        (
                                Collectors.toMap(
                                        InteractionCountProjection::getEventId,
                                        InteractionCountProjection::getScore
                                )
                        );
        return eventIds.stream()
                .map(id -> RecommendedEventProto.newBuilder()
                        .setEventId(id)
                        .setScore(counts.getOrDefault(id, 0.0))
                        .build())
                .toList();
    }
}
