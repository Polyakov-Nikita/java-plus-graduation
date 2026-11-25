package ru.practicum.explorewithme.collector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.message.UserActionProto;
import ru.practicum.explorewithme.collector.mapper.UserActionMapper;
import ru.practicum.explorewithme.common.kafka.producer.StatsProducer;

@Service
@RequiredArgsConstructor
public class CollectorGrpcService {
    private final UserActionMapper userActionMapper;
    private final StatsProducer producer;

    public void collectUserAction(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = userActionMapper.toUserActionAvro(userActionProto);
        producer.send(userActionMapper.toRecordPayload(userActionAvro));
    }
}
