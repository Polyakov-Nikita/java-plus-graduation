package ru.practicum.explorewithme.event.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.common.dto.EventStatus;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "conf_req")
    @Builder.Default
    private int confirmedRequests = 0;

    @Column(name = "created", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId;

    @Embedded
    private LocationEmbeddable location;

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "published")
    @Builder.Default
    private LocalDateTime publishedOn = null;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @Column(name = "title", nullable = false)
    private String title;
}
