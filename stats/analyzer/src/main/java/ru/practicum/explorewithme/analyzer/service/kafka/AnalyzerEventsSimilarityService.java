package ru.practicum.explorewithme.analyzer.service.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.explorewithme.analyzer.entity.EventsSimilarity;
import ru.practicum.explorewithme.analyzer.mapper.EventsSimilarityMapper;
import ru.practicum.explorewithme.analyzer.repository.EventsSimilarityRepository;
import ru.practicum.explorewithme.common.kafka.service.KafkaService;

@Service
@RequiredArgsConstructor
public class AnalyzerEventsSimilarityService implements KafkaService {
    private final EventsSimilarityMapper eventsSimilarityMapper;
    private final EventsSimilarityRepository eventsSimilarityRepository;

    @Override
    @Transactional
    public void handle(SpecificRecordBase record) {
        EventSimilarityAvro eventsSimilarityAvro = (EventSimilarityAvro) record;
        EventsSimilarity similarity = eventsSimilarityMapper.toEventsSimilarity(eventsSimilarityAvro);
        eventsSimilarityRepository.upsert(similarity);
    }
}
