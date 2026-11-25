package ru.practicum.explorewithme.deserialization;

import ru.practicum.ewm.stats.avro.UserActionAvro;

@SuppressWarnings("unused")
public class UserActionDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}
