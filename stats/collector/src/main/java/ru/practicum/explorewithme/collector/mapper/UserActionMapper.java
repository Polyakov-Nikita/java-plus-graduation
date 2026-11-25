package ru.practicum.explorewithme.collector.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.message.ActionTypeProto;
import ru.practicum.ewm.stats.proto.message.UserActionProto;
import ru.practicum.explorewithme.common.kafka.producer.RecordPayload;

import java.time.Instant;

@Component
public class UserActionMapper {
    public UserActionAvro toUserActionAvro(UserActionProto userActionProto) {
        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(toActionTypeAvro(userActionProto.getActionType()))
                .setTimestamp(toInstant(userActionProto.getTimestamp()))
                .build();
    }

    private ActionTypeAvro toActionTypeAvro(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> null;
        };
    }

    private Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public RecordPayload toRecordPayload(UserActionAvro userActionAvro) {
        return RecordPayload.builder()
                .timestamp(userActionAvro.getTimestamp())
                .key(userActionAvro.getEventId())
                .value(userActionAvro)
                .build();
    }
}
