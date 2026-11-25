package ru.practicum.explorewithme.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

@ConfigurationProperties("score")
@RequiredArgsConstructor
public class ScoreProperties {
    private final double viewScore;
    private final double registerScore;
    private final double likeScore;

    public double getScore(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ActionTypeAvro.VIEW -> viewScore;
            case ActionTypeAvro.REGISTER -> registerScore;
            case ActionTypeAvro.LIKE -> likeScore;
        };
    }
}
