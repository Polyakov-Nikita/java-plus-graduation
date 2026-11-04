package ru.practicum.explorewithme.event.mapper;

import ru.practicum.explorewithme.common.dto.Location;
import ru.practicum.explorewithme.event.entity.LocationEmbeddable;

public class LocationMapper {
    public static Location toLocation(LocationEmbeddable locationEmbeddable) {
        return new Location(locationEmbeddable.getLat(), locationEmbeddable.getLon());
    }

    public static LocationEmbeddable toLocationEmbeddable(Location location) {
        return new LocationEmbeddable(location.lat(), location.lon());
    }
}
