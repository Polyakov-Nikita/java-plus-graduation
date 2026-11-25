package ru.practicum.explorewithme.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events_similarities", schema = "public")
@IdClass(EventsSimilarityId.class)
public class EventsSimilarity {
    @Id
    @Column(name = "event_a", nullable = false)
    private Long eventA;
    @Id
    @Column(name = "event_b", nullable = false)
    private Long eventB;
    @Column(name = "similarity", nullable = false)
    private Double similarity;
    @Column(name = "action_time", nullable = false)
    private Instant actionTime;
}
