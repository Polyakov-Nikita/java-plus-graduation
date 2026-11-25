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
@Table(name = "user_actions", schema = "public")
@IdClass(UserActionId.class)
public class UserAction {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Id
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    @Column(name = "rating", nullable = false)
    private Double rating;
    @Column(name = "action_time", nullable = false)
    private Instant actionTime;
}
