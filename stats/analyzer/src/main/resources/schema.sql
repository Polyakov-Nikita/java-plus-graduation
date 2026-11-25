CREATE TABLE IF NOT EXISTS user_actions (
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    action_time TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS events_similarities (
    event_a BIGINT NOT NULL,
    event_b BIGINT NOT NULL,
    similarity DOUBLE PRECISION NOT NULL,
    action_time TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (event_a, event_b)
);