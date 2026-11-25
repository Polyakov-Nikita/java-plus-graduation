package ru.practicum.explorewithme.analyzer.service.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.explorewithme.analyzer.entity.UserAction;
import ru.practicum.explorewithme.analyzer.mapper.UserActionMapper;
import ru.practicum.explorewithme.analyzer.repository.UserActionRepository;
import ru.practicum.explorewithme.common.kafka.service.KafkaService;

@Service
@RequiredArgsConstructor
public class AnalyzerUserActionsService implements KafkaService {
    private final UserActionMapper userActionMapper;
    private final UserActionRepository userActionRepository;

    @Override
    @Transactional
    public void handle(SpecificRecordBase record) {
        UserActionAvro actionAvro = (UserActionAvro) record;
        UserAction action = userActionMapper.toUserAction(actionAvro);
        userActionRepository.upsert(action);
    }
}
