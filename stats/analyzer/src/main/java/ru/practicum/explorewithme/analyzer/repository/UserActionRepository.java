package ru.practicum.explorewithme.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.analyzer.entity.UserAction;
import ru.practicum.explorewithme.analyzer.entity.UserActionId;
import ru.practicum.explorewithme.analyzer.repository.projection.InteractionCountProjection;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, UserActionId> {
    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_actions (user_id, event_id, rating, action_time)
            VALUES (:#{#action.userId}, :#{#action.eventId}, :#{#action.rating}, :#{#action.actionTime})
            ON CONFLICT (user_id, event_id)
            DO UPDATE SET rating = GREATEST(user_actions.rating, EXCLUDED.rating),
            action_time = CASE
            WHEN EXCLUDED.rating > user_actions.rating
            THEN EXCLUDED.action_time
            ELSE user_actions.action_time
            END
            """)
    void upsert(@Param("action") UserAction action);

    @Query(nativeQuery = true, value = """
            SELECT event_id,
                   SUM(rating) AS score
            FROM user_actions
            WHERE event_id IN (:eventIds)
            GROUP BY event_id
            """)
    List<InteractionCountProjection> countInteractions(@Param("eventIds") List<Long> eventIds);

}
