package ru.practicum.explorewithme.analyzer.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.explorewithme.analyzer.entity.UserAction;
import ru.practicum.explorewithme.common.config.ScoreProperties;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(ScoreProperties.class)
public class UserActionMapper {
    private final ScoreProperties scoreProperties;

    public UserAction toUserAction(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .rating(scoreProperties.getScore(userActionAvro.getActionType()))
                .actionTime(userActionAvro.getTimestamp())
                .build();
    }
}
