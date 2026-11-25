package ru.practicum.explorewithme.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.analyzer.entity.EventsSimilarity;
import ru.practicum.explorewithme.analyzer.entity.EventsSimilarityId;
import ru.practicum.explorewithme.analyzer.repository.projection.RecommendedEventProjection;

import java.util.List;

public interface EventsSimilarityRepository extends JpaRepository<EventsSimilarity, EventsSimilarityId> {
    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO events_similarities (event_a, event_b, similarity, action_time)
            VALUES (:#{#similarity.eventA}, :#{#similarity.eventB}, :#{#similarity.similarity}, :#{#similarity.actionTime})
            ON CONFLICT (event_a, event_b)
            DO UPDATE SET similarity = EXCLUDED.similarity,
            action_time = EXCLUDED.action_time
            """)
    void upsert(@Param("similarity") EventsSimilarity similarity);

    @Query(nativeQuery = true, value = """
            WITH user_events AS (
                SELECT event_id FROM user_actions WHERE user_id = :userId
            ),
            candidates AS (
                SELECT s.event_a AS event_id, s.similarity
                FROM events_similarities s
                WHERE s.event_b IN (SELECT event_id FROM user_events)
                  AND s.event_a NOT IN (SELECT event_id FROM user_events)
                UNION ALL
                SELECT s.event_b, s.similarity
                FROM events_similarities s
                WHERE s.event_a IN (SELECT event_id FROM user_events)
                  AND s.event_b NOT IN (SELECT event_id FROM user_events)
            )
            SELECT event_id, MAX(similarity) AS similarity
            FROM candidates
            GROUP BY event_id
            ORDER BY similarity DESC
            LIMIT :maxResults
            """)
    List<RecommendedEventProjection> findRecommendationsForUser(
            @Param("userId") long userId,
            @Param("maxResults") int maxResults
    );

    @Query(nativeQuery = true, value = """
            SELECT (CASE WHEN s.event_a = :eventId THEN s.event_b ELSE s.event_a END) AS event_id,
                   s.similarity
            FROM events_similarities s
            WHERE (s.event_a = :eventId OR s.event_b = :eventId)
              AND (CASE WHEN s.event_a = :eventId THEN s.event_b ELSE s.event_a END) NOT IN (
                  SELECT ua.event_id FROM user_actions ua WHERE ua.user_id = :userId
              )
            ORDER BY s.similarity DESC
            LIMIT :maxResults
            """)
    List<RecommendedEventProjection> findSimilarEvents(
            @Param("eventId") long eventId,
            @Param("userId") long userId,
            @Param("maxResults") int maxResults
    );
}
